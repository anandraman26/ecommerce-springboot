package com.ecommerce.inventory.service;

import com.ecommerce.inventory.Mapper.InventoryMapper;
import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.entity.PendingInventoryAction;
import com.ecommerce.inventory.entity.ProcessedOrder;
import com.ecommerce.inventory.event.OrderPlacedEvent;
import com.ecommerce.inventory.exception.InventoryNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.PendingInventoryActionRepository;
import com.ecommerce.inventory.repository.ProcessedOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private static final String RESERVED = "RESERVED";
    private static final String COMMITTED = "COMMITTED";
    private static final String ROLLED_BACK = "ROLLED_BACK";
    private static final String COMMIT = "COMMIT";
    private static final String ROLLBACK = "ROLLBACK";

    private final InventoryRepository inventoryRepo;
    private final ProcessedOrderRepository processedOrderRepo;
    private final PendingInventoryActionRepository pendingActionRepo;
    private final InventoryMapper inventoryMapper;

    public InventoryResponse checkInventory(String skuCode) {
        Inventory inventory = inventoryRepo.findBySkuCode(skuCode)
                .orElseThrow(() -> new InventoryNotFoundException(skuCode));
        return inventoryMapper.toresponse(inventory);
    }

    public void addInventory(InventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        inventoryRepo.save(inventory);
    }

    /**
     * Kafka consumer entry point.
     * Decrements stock exactly once and creates a RESERVED record.
     * A later payment callback commits or rolls the reservation back.
     */
    @Transactional
    public void updateStock(OrderPlacedEvent event) {
        //Here we need to check this order is already placed or not
        // this is called idempotence case means order already placed so no duplicate order will create
        //for that we need to create processed Order table which will maintain this
        if (processedOrderRepo.existsByOrderId(event.getOrderId())) {
            return;
        }

        PendingInventoryAction pending = pendingActionRepo
                .findByOrderId(event.getOrderId())
                .orElse(null);

        // If payment failure arrived before Kafka, don't reserve stock at all.
        if (pending != null && ROLLBACK.equals(pending.getAction())) {
            ProcessedOrder processed = newProcessedOrder(
                    event, ROLLED_BACK);
            processedOrderRepo.save(processed);
            pendingActionRepo.delete(pending);
            return;
        }

        Inventory inventory = inventoryRepo.findBySkuCode(event.getSkuCode())
                .orElseThrow(() -> new InventoryNotFoundException(event.getSkuCode()));

        int availableQuantity = inventory.getQuantity();
        int orderedQuantity = event.getQuantity();

        if (availableQuantity < orderedQuantity) {
            throw new IllegalStateException(
                    "Insufficient stock for skuCode: " + event.getSkuCode());
        }

        inventory.setQuantity(availableQuantity - orderedQuantity);
        inventoryRepo.save(inventory);

        String status = RESERVED;
        if (pending != null && COMMIT.equals(pending.getAction())) {
            status = COMMITTED;
        }

        processedOrderRepo.save(newProcessedOrder(event, status));

        if (pending != null) {
            pendingActionRepo.delete(pending);
        }
    }

    /**
     * Payment succeeded. A RESERVED inventory record becomes COMMITTED.
     * If Kafka has not arrived yet, store the action and let the Kafka
     * consumer apply it when the reservation is created.
     */
    @Transactional
    public void commitInventory(String orderId) {
        ProcessedOrder processed = processedOrderRepo.findByOrderId(orderId).orElse(null);

        if (processed == null) {
            savePendingAction(orderId, COMMIT);
            return;
        }

        if (COMMITTED.equals(processed.getStatus())) {
            return;
        }

        if (ROLLED_BACK.equals(processed.getStatus())) {
            throw new IllegalStateException(
                    "Cannot commit rolled back inventory for order: " + orderId);
        }

        processed.setStatus(COMMITTED);
        processed.setProcessedAt(LocalDateTime.now());
        processedOrderRepo.save(processed);
    }

    /**
     * Payment failed. A RESERVED inventory record is restored.
     * If Kafka has not arrived yet, store the action so the later Kafka
     * consumer will not reserve stock.
     */
    @Transactional
    public void rollbackInventory(String orderId) {
        ProcessedOrder processed = processedOrderRepo.findByOrderId(orderId).orElse(null);

        if (processed == null) {
            savePendingAction(orderId, ROLLBACK);
            return;
        }

        if (ROLLED_BACK.equals(processed.getStatus())) {
            return;
        }

        if (COMMITTED.equals(processed.getStatus())) {
            throw new IllegalStateException(
                    "Cannot rollback committed inventory for order: " + orderId);
        }

        Inventory inventory = inventoryRepo.findBySkuCode(processed.getSkuCode())
                .orElseThrow(() -> new InventoryNotFoundException(processed.getSkuCode()));

        inventory.setQuantity(inventory.getQuantity() + processed.getQuantity());
        inventoryRepo.save(inventory);

        processed.setStatus(ROLLED_BACK);
        processed.setProcessedAt(LocalDateTime.now());
        processedOrderRepo.save(processed);
    }

    private void savePendingAction(String orderId, String action) {
        PendingInventoryAction pending = pendingActionRepo
                .findByOrderId(orderId)
                .orElse(null);

        if (pending == null) {
            pendingActionRepo.save(
                    new PendingInventoryAction(
                            null,
                            orderId,
                            action,
                            LocalDateTime.now()));
            return;
        }

        // Same action is already safely recorded; different actions indicate
        // conflicting payment callbacks and must not silently overwrite state.
        if (!action.equals(pending.getAction())) {
            throw new IllegalStateException(
                    "Conflicting inventory actions for order: " + orderId);
        }
    }

    private ProcessedOrder newProcessedOrder(OrderPlacedEvent event, String status) {
        ProcessedOrder processed = new ProcessedOrder();
        processed.setOrderId(event.getOrderId());
        processed.setSkuCode(event.getSkuCode());
        processed.setQuantity(event.getQuantity());
        processed.setStatus(status);
        processed.setProcessedAt(LocalDateTime.now());
        return processed;
    }
}
