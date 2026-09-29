package com.ecommerce.order.kafka;


import com.ecommerce.order.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventProducer {

    private static final String NOTIFICATION_TOPIC = "notification-topic";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void sendNotification(NotificationEvent event) {

        kafkaTemplate.send(
                NOTIFICATION_TOPIC,
                event.getOrderId(),
                event
        );
    }
}
