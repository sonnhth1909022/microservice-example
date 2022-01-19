package com.microservice.orderservice.service;


import com.microservice.orderservice.controller.CartController;
import com.microservice.orderservice.dto.OrderDto;
import com.microservice.orderservice.dto.PaymentDto;
import com.microservice.orderservice.entity.Cart;
import com.microservice.orderservice.entity.Order;
import com.microservice.orderservice.enums.Status;
import com.microservice.orderservice.repo.OrderRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.microservice.orderservice.queue.Config.DIRECT_EXCHANGE;
import static com.microservice.orderservice.queue.Config.DIRECT_ROUTING_KEY_ORDER;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartController cartController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Order create(@RequestBody Order order) {
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (Cart cart : CartController.cartHashMap.values()) {
            totalPrice = totalPrice.add(cart.getUnitPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDate.now());
        order.setPaymentStatus(Status.Payment.UNPAID.name());
        order.setOrderStatus(Status.Order.PENDING.name());
        Order orderSave;
        try {
            orderSave = orderRepo.save(order);
            System.out.println(orderSave);
            System.out.println(new OrderDto(orderSave));
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, new OrderDto(orderSave));
            cartController.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return orderSave;
    }

    @Override
    public Page<Order> getAll(int page, int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        if (pageSize < 0) {
            page = 6;
        }
        return orderRepo.findAll(PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id"));
    }

    @Override
    @Transactional
    public void handlerOrderPayment(PaymentDto paymentDto) {

        if (!validationPaymentDto(paymentDto)) return;

        Order orderExist = orderRepo.findById(paymentDto.getOrderId()).orElse(null);
        if (orderExist == null) {
            System.out.println("Hoá đơn không tìm thấy.");
            return;
        }

        if (paymentDto.getPaymentStatus().equals(Status.Payment.UNPAID.name())) {
            System.out.println("Thanh toán lỗi");
            return;
        }
        try {
            if (paymentDto.getPaymentStatus().equals(Status.Payment.PAID.name())) {
                System.out.println("Thanh toán hoá đơn thành công");
                orderExist.setPaymentStatus(Status.Payment.PAID.name());
                orderRepo.save(orderExist);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean validationPaymentDto(PaymentDto paymentDto) {
        return paymentDto.getPaymentStatus() != null
                && paymentDto.getUserId() != null
                && paymentDto.getOrderId() != null;
    }

}
