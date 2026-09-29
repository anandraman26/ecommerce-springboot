package com.ecommerce.inventory.event;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class OrderPlacedEvent {
	private String eventId;
	private String orderId;
	private String skuCode;
	private Integer quantity;
	private LocalDateTime eventTime;
}
