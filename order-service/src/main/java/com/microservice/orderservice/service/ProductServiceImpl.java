package com.microservice.orderservice.service;

import com.microservice.orderservice.entity.Product;
import com.microservice.orderservice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepo productRepo;

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepo.findById(id);
    }
}
