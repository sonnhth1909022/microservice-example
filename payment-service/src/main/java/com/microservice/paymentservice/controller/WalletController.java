package com.microservice.paymentservice.controller;

import com.microservice.paymentservice.entity.TransactionHistory;
import com.microservice.paymentservice.entity.Wallet;
import com.microservice.paymentservice.repo.TransactionRepo;
import com.microservice.paymentservice.repo.WalletRepo;
import com.microservice.paymentservice.response.RESTResponse;
import com.microservice.paymentservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/payments/")
public class WalletController {

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    WalletService walletService;

    @RequestMapping(path = "account/{userId}", method = RequestMethod.GET)
    public ResponseEntity find(@PathVariable int userId) {
        Wallet wallet = walletRepo.findWalletByUserId((long) userId);
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(wallet)
                .build(), HttpStatus.OK);
    }

    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public ResponseEntity send(@Valid @RequestBody TransactionHistory history) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(walletService.transfer(history))
                .build(), HttpStatus.OK);
    }


}
