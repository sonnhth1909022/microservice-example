package com.microservice.orderservice.queue;


import com.microservice.orderservice.controller.OrderController;
import com.microservice.orderservice.dto.PaymentDto;
import com.microservice.orderservice.entity.Order;
import com.microservice.orderservice.fcm.FCMService;
import com.microservice.orderservice.fcm.PnsRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.microservice.orderservice.queue.Config.QUEUE_PAY;


@Component
public class ReceiveMessage {

    @Autowired
    OrderController orderController;

    @Autowired
    private FCMService fcmService;

    @RabbitListener(queues = {QUEUE_PAY})
    public void getInfoPayment(PaymentDto paymentDto) {
        Order order =orderController.handlerOrder(paymentDto);
        System.out.println("Module Order nhận thông tin thanh toán thành công: "
                + order);
        PnsRequest pnsRequest = new PnsRequest();
        pnsRequest.setFcmToken(paymentDto.getDevice_token());
        pnsRequest.setContent(paymentDto.getMessage());
        pnsRequest.setTitle("Order " + paymentDto.getOrderId());
        fcmService.pushNotification(pnsRequest);
    }

}
