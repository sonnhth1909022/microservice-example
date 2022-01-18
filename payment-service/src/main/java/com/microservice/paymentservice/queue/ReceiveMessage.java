package com.microservice.paymentservice.queue;

import com.microservice.paymentservice.controller.WalletController;
import com.microservice.paymentservice.dto.OrderDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.microservice.paymentservice.queue.Config.QUEUE_ORDER;


@Component
public class ReceiveMessage {

    @Autowired
    WalletController balletController;

    @RabbitListener(queues = {QUEUE_ORDER})
    public void getInfoOrder(OrderDto orderDto) {
        System.out.println("Module Payment nhận thông tin order: " + orderDto);
        balletController.handlerPayment(orderDto);
    }

}
