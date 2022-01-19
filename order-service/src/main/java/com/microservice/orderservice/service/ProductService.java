package com.microservice.orderservice.service;

import com.microservice.orderservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface ProductService {
    Page<Product> getAllProducts(Pageable pageable);
    Optional<Product> findProductById(Long id);
}
