package com.ecommerce.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private String eventId;

    private String orderId;

    private Long userId;

    private String email;

    private String mobile;

    private String skuCode;

    private Integer quantity;

    private LocalDateTime eventTime;
}