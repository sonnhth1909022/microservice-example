package com.microservice.paymentservice.entity;

import com.microservice.paymentservice.enums.PaymentType;
import com.microservice.paymentservice.enums.TransactionStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory extends BaseEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id",columnDefinition = "VARCHAR(50)")
    private String id;

    private Long senderId;
    private Long receiverId;
    private Long orderId;
    private String paymentType;
    private String transactionStatus;
    private int amount;
    private String message;
}
