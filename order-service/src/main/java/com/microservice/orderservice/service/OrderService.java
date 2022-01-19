package com.microservice.orderservice.service;

import com.microservice.orderservice.dto.PaymentDto;
import com.microservice.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    Order create(@RequestBody Order order);

    Page<Order> getAll(int page, int pageSize);

    @Transactional
    void handlerOrderPayment(PaymentDto paymentDto);
}
