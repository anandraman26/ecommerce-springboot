package com.ecommerce.order.service;

import com.ecommerce.order.client.InventoryFeignClient;
import com.ecommerce.order.client.UserFeignClient;
import com.ecommerce.order.dto.InventoryResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.event.NotificationEvent;
import com.ecommerce.order.event.OrderPlacedEvent;
import com.ecommerce.order.kafka.NotificationEventProducer;
import com.ecommerce.order.kafka.OrderEventProducer;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.order.Order;
import com.ecommerce.order.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private final UserFeignClient userFeignClient;
    private final NotificationEventProducer notificationEventProducer;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        /*
         * ---------------------------------------------------------
         * 1. Get logged-in user
         * ---------------------------------------------------------
         *
         * X-User-Id is added by API Gateway after JWT validation.
         *
         * The Order Service should NOT trust a userId supplied
         * directly by the client.
         */

        // We will get this from the authenticated request.
        Long userId = request.getUserId();//getCurrentUserId();

        user = userFeignClient.getUserById(userId);

        /*
         * ---------------------------------------------------------
         * 2. Check inventory
         * ---------------------------------------------------------
         */
        //This will first check the inventory service whether product is available in inventory
        // First check inventory availability. Actual reservation is performed
        // asynchronously by Inventory Service when it consumes order-event.
        InventoryResponse inventory = inventoryFeignClient.isInStock(request.getSkuCode());
        if (!inventory.isInStock()) {
            throw new IllegalStateException("product is out of stock");
        }

        /*
         * ---------------------------------------------------------
         * 3. Create order
         * ---------------------------------------------------------
         */
        Order order = orderMapper.toEntity(request);
        String orderId = UUID.randomUUID().toString();
        order.setOrderNumber(orderId);
        order.setOrderStatus(CREATED);
        orderRepo.save(order);

        /*
         * ---------------------------------------------------------
         * 4. Publish OrderPlacedEvent
         * ---------------------------------------------------------
         *
         * Inventory Service consumes this event.
         */

        //Kafka Trigger
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setEventId(Uuid.randomUuid().toString());
        event.setOrderId(orderId);
        event.setSkuCode(request.getSkuCode());
        event.setQuantity(request.getQuantity());
        event.setEventTime(LocalDateTime.now());
        orderEventProducer.sendOrderEvent(event);
        /*
         * ---------------------------------------------------------
         * 5. Publish Notification Event
         * ---------------------------------------------------------
         *
         * Notification Service consumes this event.
         */

        sendSmsAndEmailNotification(orderId, user);

        /*
         * ---------------------------------------------------------
         * 5. Publish Notification Event
         * ---------------------------------------------------------
         *
         * Notification Service consumes this event.
         */

        sendSmsAndEmailNotification(orderId, user);

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

    private void sendSmsAndEmailNotification(String orderId, UserResponse user) {
        NotificationEvent emailEvent =
                NotificationEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .orderId(orderId)
                        .email(user.getEmail())
                        .subject("Order Placed Successfully")
                        .message(
                                "Your order " + orderId +
                                        " has been placed successfully."
                        )
                        .type("EMAIL")
                        .build();

        notificationEventProducer.sendNotification(emailEvent);


        NotificationEvent smsEvent =
                NotificationEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .orderId(orderId)
                        .mobile(user.getPhone())
                        .message(
                                "Your order " + orderId +
                                        " has been placed successfully."
                        )
                        .type("SMS")
                        .build();

        notificationEventProducer.sendNotification(smsEvent);
    }
}
