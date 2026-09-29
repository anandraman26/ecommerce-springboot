package com.ecommerce.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventId;

    private String orderId;

    private String email;

    private String mobile;

    private String subject;

    private String message;

    /**
     * EMAIL or SMS
     */
    private String type;
}
