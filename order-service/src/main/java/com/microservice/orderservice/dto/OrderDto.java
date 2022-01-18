package com.microservice.orderservice.dto;

import com.microservice.orderservice.entity.Order;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private Long orderId;
    private Long userId;
    private double totalPrice;
    private String paymentStatus;
    private String orderStatus;
    private String device_token;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus();
        this.paymentStatus = order.getPaymentStatus();
        this.device_token = order.getDevice_token();
    }
}
