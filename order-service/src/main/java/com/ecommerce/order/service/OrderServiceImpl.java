package com.ecommerce.order.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.springframework.stereotype.Service;

import com.ecommerce.order.client.InventoryFeignClient;
import com.ecommerce.order.dto.InventoryResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.event.OrderPlacedEvent;
import com.ecommerce.order.kafka.OrderEventProducer;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.order.Order;
import com.ecommerce.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
		private final OrderRepository orderRepo;
		private final OrderMapper orderMapper;
		private final OrderEventProducer orderEventProducer;
		private final InventoryFeignClient inventoryFeignClient;
		@Override
		public OrderResponse PlaceOrder(OrderRequest request) {
			InventoryResponse inventory = inventoryFeignClient.isInStock(request.getSkuCode());
			if(!inventory.isInStock()) {
				throw new RuntimeException("product is out of stock");
			}
			Order order = orderMapper.toEntity(request);
			String orderId = UUID.randomUUID().toString();
			order.setOrderNumber(orderId);
			order.setOrderStatus("CREATED");
			orderRepo.save(order);
			OrderPlacedEvent event = new OrderPlacedEvent();
			event.setEventId(Uuid.randomUuid().toString());
			event.setOrderId(orderId);
			event.setSkuCode(request.getSkuCode());
			event.setQuantity(request.getQuantity());
			
			event.setEventTime(LocalDateTime.now());
			orderEventProducer.sendOrderEvent(event);
			return orderMapper.toResponse(order);
		}
}
