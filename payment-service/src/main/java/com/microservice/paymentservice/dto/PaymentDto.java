package com.microservice.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDto {

    private Long orderId;
    private Long userId;
    private String paymentStatus;
    private String message;
    private String device_token;


    public PaymentDto(Long orderId, Long userId, String device_token) {
        this.orderId = orderId;
        this.userId = userId;
        this.device_token = device_token;
    }
}
