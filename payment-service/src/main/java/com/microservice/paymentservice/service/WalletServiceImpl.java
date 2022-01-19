package com.microservice.paymentservice.service;

import com.microservice.paymentservice.dto.OrderDto;
import com.microservice.paymentservice.dto.PaymentDto;
import com.microservice.paymentservice.dto.TransactionDto;
import com.microservice.paymentservice.entity.TransactionHistory;
import com.microservice.paymentservice.entity.Wallet;
import com.microservice.paymentservice.enums.PaymentType;
import com.microservice.paymentservice.enums.Status;
import com.microservice.paymentservice.exception.NotFoundException;
import com.microservice.paymentservice.repo.TransactionRepo;
import com.microservice.paymentservice.repo.WalletRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.microservice.paymentservice.queue.Config.*;


@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public void handlerPayment(OrderDto orderDto) {
        PaymentDto paymentDto = new PaymentDto(orderDto.getOrderId(),
                orderDto.getUserId(), orderDto.getDevice_token());

        if (orderDto.getPaymentStatus() == null) {
            paymentDto.setMessage("Trạng thái thanh toán không xác định");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
            return;
        }

        if (orderDto.getPaymentStatus().equals(Status.Payment.REFUND.name())) {
            handlerOrderRefund(orderDto, paymentDto);
            return;
        }

        Wallet wallet = checkWallet(orderDto, paymentDto);
        if (wallet == null) return;

        double totalPrice = orderDto.getTotalPrice();
        double balance = wallet.getBalance();

        if (totalPrice > balance) {
            paymentDto.setMessage("Số dư ví không đủ");
            paymentDto.setPaymentStatus(Status.Payment.UNPAID.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
            return;
        }

        TransactionHistory history = TransactionHistory.Builder
                .aTransactionHistory()
                .withSenderId(orderDto.getUserId())
                .withOrderId(orderDto.getOrderId())
                .withAmount(orderDto.getTotalPrice())
                .withPaymentType(PaymentType.SENDING.name())
                .build();

        try {
            wallet.setBalance(balance - totalPrice);
            history.setStatus(Status.Transaction.SUCCESS.name());
            paymentDto.setPaymentStatus(Status.Payment.PAID.name());
            paymentDto.setMessage("Thanh toán thành công");
            walletRepo.save(wallet);
            transactionRepo.save(history);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
        } catch (Exception e) {
            history.setStatus(Status.Transaction.FAIL.name());
            transactionRepo.save(history);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderDto);
            throw new RuntimeException("thanh toán lỗi vui lòng thử lại.");
        }
    }

    @Transactional
    void handlerOrderRefund(OrderDto orderDto, PaymentDto paymentDto) {
        Wallet wallet = walletRepo.findWalletByUserId(orderDto.getUserId());
        TransactionHistory history = TransactionHistory.Builder.aTransactionHistory()
                .withSenderId(orderDto.getUserId())
                .withOrderId(orderDto.getOrderId())
                .withAmount(orderDto.getTotalPrice())
                .withPaymentType(PaymentType.REFUND.name())
                .build();

        try {
            wallet.setBalance(wallet.getBalance() + orderDto.getTotalPrice());
            history.setStatus(Status.Transaction.SUCCESS.name());
            paymentDto.setPaymentStatus(Status.Payment.REFUNDED.name());
            walletRepo.save(wallet);
            transactionRepo.save(history);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
        } catch (Exception e) {
            history.setStatus(Status.Transaction.FAIL.name());
            transactionRepo.save(history);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderDto);
            throw new RuntimeException("refund order fail.");
        }
    }

    private Wallet checkWallet(OrderDto orderDto, PaymentDto paymentDto) {
        if (orderDto.getUserId() == null) {
            paymentDto.setMessage("Tài khoản ví không được trống");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
            return null;
        }

        Wallet wallet = walletRepo.findWalletByUserId(orderDto.getUserId());
        if (wallet == null) {
            paymentDto.setMessage("Tài khoản thanh toán không đúng");
            paymentDto.setPaymentStatus(Status.Payment.UNPAID.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, paymentDto);
            return null;
        }
        return wallet;
    }


    @Transactional
    @Override
    public TransactionDto transfer(TransactionHistory history) {
        TransactionDto dto = new TransactionDto();
        TransactionHistory historySave = TransactionHistory.Builder.aTransactionHistory()
                .withSenderId(history.getSenderId())
                .withReceiverId(history.getReceiverId())
                .withMessage(history.getMessage())
                .withPaymentType(PaymentType.SENDING.name())
                .build();
        try {

            if (history.getAmount() <= 0) {
                historySave.setStatus(Status.Transaction.FAIL.name());
                transactionRepo.save(historySave);
                throw new RuntimeException("Số tiền phải lớn hơn 0");
            }
            Wallet walletSender = walletRepo.findWalletByUserId(history.getSenderId());
            Wallet walletReceiver = walletRepo.findWalletByUserId(history.getReceiverId());

            if (walletSender == null) throw new NotFoundException("wallet sender not found!");
            if (walletReceiver == null) throw new NotFoundException("wallet receiver not found!");
            if (walletSender.getBalance() < history.getAmount()) throw new RuntimeException("Tài khoản không đủ");


            walletSender.setBalance(walletSender.getBalance() - history.getAmount());
            walletReceiver.setBalance(walletReceiver.getBalance() + history.getAmount());
            dto.setSender(walletSender.getName());
            dto.setReceiver(walletReceiver.getName());
            dto.setMessage(history.getMessage());
            dto.setAmount(history.getAmount());

            walletRepo.save(walletSender);
            walletRepo.save(walletReceiver);
            transactionRepo.save(historySave);
        } catch (Exception e) {
            historySave.setStatus(Status.Transaction.FAIL.name());
            transactionRepo.save(historySave);
            throw new RuntimeException(e.getMessage());
        }

        return dto;
    }
}
