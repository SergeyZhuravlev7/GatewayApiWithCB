package ru.aston.GatewayApi.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SMSNotificationServiceStub {

    public ResponseEntity<?> sendNotification() {
        //логика отправки смс уведомления
        return ResponseEntity
                .ok()
                .build();
    }
}
