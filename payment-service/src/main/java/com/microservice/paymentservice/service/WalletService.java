package com.microservice.paymentservice.service;

import com.microservice.paymentservice.dto.OrderDto;
import com.microservice.paymentservice.dto.TransactionDto;
import com.microservice.paymentservice.entity.TransactionHistory;
import org.springframework.transaction.annotation.Transactional;

public interface WalletService {
    @Transactional
    void handlerPayment(OrderDto orderDto);

    @Transactional
    TransactionDto transfer(TransactionHistory history);
}
