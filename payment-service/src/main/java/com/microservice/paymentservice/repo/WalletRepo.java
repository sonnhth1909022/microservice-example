package com.microservice.paymentservice.repo;

import com.microservice.paymentservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    Wallet findBalletByUserId(Long id);
}
