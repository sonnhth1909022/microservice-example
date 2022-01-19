package com.microservice.orderservice.entity;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@ToString
public class Order extends BaseEntity{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private BigDecimal totalPrice;
    private String address;
    private String name;
    private String phone;
    private String email;
    private String paymentStatus;
    private String orderStatus;



}
