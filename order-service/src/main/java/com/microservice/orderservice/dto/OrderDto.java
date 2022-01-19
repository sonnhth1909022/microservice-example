package com.microservice.orderservice.dto;

import com.microservice.orderservice.entity.Order;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String orderStatus;
    private String device_token;
//    private HashMap<Long, Integer> productAndQuantity;


    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.paymentStatus = order.getPaymentStatus();
        this.orderStatus = order.getOrderStatus();
    }
}
