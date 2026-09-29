package com.ecommerce.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.inventory.entity.Inventory;
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{
		Optional<Inventory> findBySkuCode(String skuCode);
}
