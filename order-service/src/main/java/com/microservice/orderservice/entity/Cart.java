package com.microservice.orderservice.entity;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cart {

    private Long productId;
    private String name;
    private int quantity;
    private String thumbnail;
    private double unitPrice;

}
