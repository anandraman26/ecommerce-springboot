package com.ecommerce.notification.consumer;

import com.ecommerce.notification.dto.NotificationEvent;
import com.ecommerce.notification.service.EmailService;
import com.ecommerce.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        if ("EMAIL".equals(event.getType())) {
            emailService.sendMail(event.getEmail(), event.getSubject(), event.getMessage());
        }
        if ("SMS".equals(event.getType())) {
            smsService.sendSms(event.getMobile(), event.getMessage());
        }
    }
}
