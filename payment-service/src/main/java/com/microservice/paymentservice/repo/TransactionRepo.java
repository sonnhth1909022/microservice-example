package com.microservice.paymentservice.repo;

import com.microservice.paymentservice.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<TransactionHistory, Long> {
}
