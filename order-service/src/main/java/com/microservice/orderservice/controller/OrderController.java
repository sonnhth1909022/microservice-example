package com.microservice.orderservice.controller;


import com.microservice.orderservice.entity.Order;
import com.microservice.orderservice.response.RESTPagination;
import com.microservice.orderservice.response.RESTResponse;
import com.microservice.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartController cartController;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @RequestMapping(method = RequestMethod.POST, path = "create")
    public ResponseEntity create(@RequestBody Order order) {
        return new ResponseEntity<>(
                new RESTResponse.Success()
                        .addData(orderService.create(order))
                        .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity getAll(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "pageSize", defaultValue = "6") int pageSize
    ) {
        Page<Order> paging = orderService.getAll(page, pageSize);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setPagination(new RESTPagination(paging.getNumber() + 1, paging.getSize(), paging.getTotalElements()))
                .addData(paging.getContent())
                .buildData(), HttpStatus.OK);
    }

}
