package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.PendingInventoryAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingInventoryActionRepository extends JpaRepository<PendingInventoryAction, Long> {
    Optional<PendingInventoryAction> findByOrderId(String orderId);
}
