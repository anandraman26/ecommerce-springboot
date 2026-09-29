package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long> {
    boolean existsByOrderId(String orderId);
}
