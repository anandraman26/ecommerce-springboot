package com.ecommerce.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;

    private String orderId;

    private String email;

    private String subject;

    private String message;

    private String type;

    private String mobile;
}