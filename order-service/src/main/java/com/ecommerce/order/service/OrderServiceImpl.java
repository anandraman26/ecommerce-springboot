package com.ecommerce.order.service;

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
import org.apache.kafka.common.Uuid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final String CREATED = "CREATED";
    private static final String CONFIRMED = "CONFIRMED";
    private static final String FAILED = "FAILED";

    private final OrderRepository orderRepo;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;
    private final InventoryFeignClient inventoryFeignClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // First check inventory availability. Actual reservation is performed
        // asynchronously by Inventory Service when it consumes order-event.
        InventoryResponse inventory = inventoryFeignClient.isInStock(request.getSkuCode());
        if (!inventory.isInStock()) {
            throw new IllegalStateException("product is out of stock");
        }

        Order order = orderMapper.toEntity(request);
        String orderId = UUID.randomUUID().toString();
        order.setOrderNumber(orderId);
        order.setOrderStatus(CREATED);
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

    @Override
    @Transactional
    public void confirmOrder(String orderId) {
        Order order = findOrder(orderId);
        String currentStatus = order.getOrderStatus();

        if (CONFIRMED.equals(currentStatus)) {
            return; // idempotent webhook retry
        }

        if (FAILED.equals(currentStatus)) {
            throw new IllegalStateException(
                    "Cannot confirm failed order: " + orderId);
        }

        order.setOrderStatus(CONFIRMED);
        orderRepo.save(order);
    }

    @Override
    @Transactional
    public void failOrder(String orderId) {
        Order order = findOrder(orderId);
        String currentStatus = order.getOrderStatus();

        if (FAILED.equals(currentStatus)) {
            return; // idempotent webhook retry
        }

        if (CONFIRMED.equals(currentStatus)) {
            throw new IllegalStateException(
                    "Cannot fail confirmed order: " + orderId);
        }

        order.setOrderStatus(FAILED);
        orderRepo.save(order);
    }

    private Order findOrder(String orderId) {
        return orderRepo.findByOrderNumber(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
    }
}
