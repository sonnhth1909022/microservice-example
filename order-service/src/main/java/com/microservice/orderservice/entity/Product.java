package com.microservice.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;

    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;


    private Integer status;

    @Column(name = "is_removed")
    private int isRemoved;

    @Column(name = "created_at")
    private LocalDate createdAt;


    @Column(name = "updated_at")
    private LocalDate updatedAt;


    @Column(name = "deleted_at")
    private LocalDate deletedAt;

}
