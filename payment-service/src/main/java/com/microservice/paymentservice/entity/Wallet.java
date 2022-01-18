package com.microservice.paymentservice.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "wallets")
public class Wallet extends BaseEntity{
    @Id
    @Column(name = "id", nullable = false)
    private Long userId;
    private double balance;
    private String name;
}
