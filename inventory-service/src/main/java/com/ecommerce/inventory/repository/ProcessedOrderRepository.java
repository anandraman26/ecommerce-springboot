package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long> {
    boolean existsByOrderId(String orderId);
    Optional<ProcessedOrder> findByOrderId(String orderId);
}
