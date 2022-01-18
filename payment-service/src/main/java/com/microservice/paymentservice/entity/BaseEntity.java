package com.microservice.paymentservice.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BaseEntity {

    private int isRemove;
    private LocalDate createdAt;
    private LocalDate updateAt;
    private LocalDate deleteAt;
}
