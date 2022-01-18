package com.microservice.orderservice.controller;

import com.microservice.orderservice.entity.Cart;
import com.microservice.orderservice.entity.Product;
import com.microservice.orderservice.repo.ProductRepo;
import com.microservice.orderservice.response.RESTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("cart")
public class CartController {

    public static HashMap<Long, Cart> cartHashMap = new HashMap<>();

    @Autowired
    ProductRepo productRepo;


    //Add 1 item to cart
    @RequestMapping(method = RequestMethod.POST, path = "add")
    public ResponseEntity addToCart(@RequestParam(name = "id") int id) {
        Cart cartItem = new Cart();
        Product product = productRepo.findById((long)id).orElse(null);
        if (product == null){
            return new ResponseEntity<>(new RESTResponse.SimpleError()
                    .build(), HttpStatus.OK);
        }
        cartItem.setQuantity(1);
        cartItem.setThumbnail(product.getThumbnail());
        cartItem.setProductId(product.getId());
        cartItem.setName(product.getName());
        cartItem.setUnitPrice(product.getPrice());

        Cart cart = cartHashMap.putIfAbsent((long) id, cartItem);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartHashMap)
                .build(), HttpStatus.OK);
    }


    //delete Cart
    @RequestMapping(method = RequestMethod.DELETE, path = "clear")
    public ResponseEntity clear() {
        cartHashMap.clear();
        return new ResponseEntity<>(new RESTResponse.Success()
                .build(), HttpStatus.OK);
    }


    //Get all items in Cart (Cart Detail)
    @RequestMapping(method = RequestMethod.GET, path = "detail")
    public ResponseEntity get() {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartHashMap)
                .build(), HttpStatus.OK);
    }


    //Update cart
    @RequestMapping(method = RequestMethod.PUT, path = "update")
    public ResponseEntity update(@RequestParam(name = "productId") int productId,
                                 @RequestParam(name = "quantity") int quantity
    ) {
        Product product = productRepo.findById((long) productId).orElse(null);

        Cart cart = cartHashMap.get(Long.valueOf(productId));
        if (cart == null || product == null || quantity < 1) {
            return new ResponseEntity<>(new RESTResponse.SimpleError()
                    .build(), HttpStatus.BAD_REQUEST);
        }
        cart.setQuantity(quantity);
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartHashMap)
                .build(), HttpStatus.OK);
    }


}
