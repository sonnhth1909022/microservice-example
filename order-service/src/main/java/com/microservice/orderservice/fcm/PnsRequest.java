package com.microservice.orderservice.fcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PnsRequest {
    private String fcmToken;
    private String content;
    private String title;


}