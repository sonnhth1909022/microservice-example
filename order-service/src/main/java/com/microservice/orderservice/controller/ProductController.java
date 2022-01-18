package com.microservice.orderservice.controller;


import com.microservice.orderservice.repo.ProductRepo;
import com.microservice.orderservice.response.RESTPagination;
import com.microservice.orderservice.response.RESTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    ProductRepo productRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(@RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "pageSize", defaultValue = "9") int pageSize
    ){
        if (page <= 0 ){
            page = 1;
        }
        if (pageSize < 0){
            page = 9;
        }

        Page paging = productRepo.findAll(PageRequest.of(page - 1, pageSize));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setPagination(new RESTPagination(paging.getNumber() + 1, paging.getSize(), paging.getTotalElements()))
                .addData(paging.getContent())
                .buildData(), HttpStatus.OK);
    }
}
