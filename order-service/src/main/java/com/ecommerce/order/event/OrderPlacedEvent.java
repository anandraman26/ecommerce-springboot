package com.ecommerce.order.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderPlacedEvent {
    private String eventId;
    private String orderId;
    private String skuCode;
    private Integer quantity;
    private LocalDateTime eventTime;
}
