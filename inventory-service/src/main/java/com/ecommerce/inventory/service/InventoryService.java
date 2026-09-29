package com.ecommerce.inventory.service;

import java.time.LocalDateTime;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.ecommerce.inventory.Mapper.InventoryMapper;
import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.entity.ProcessedOrder;
import com.ecommerce.inventory.event.OrderPlacedEvent;
import com.ecommerce.inventory.exception.InventoryNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.ProcessedOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
		private final InventoryRepository inventoryRepo;
		private final ProcessedOrderRepository processedOrderRepo;
		private final InventoryMapper inventoryMapper;
		public InventoryResponse checkInventory(String skuCode) {
			Inventory inventory =  inventoryRepo.findBySkuCode(skuCode).orElseThrow(()-> new InventoryNotFoundException(skuCode));
		return inventoryMapper.toresponse(inventory);
		}
		public void addInventory(InventoryRequest request) {
			Inventory inventory = inventoryMapper.toEntity(request);
			inventoryRepo.save(inventory);
		}
		@Transactional
		public void updateStock(OrderPlacedEvent event) {
			if(processedOrderRepo.existsByOrderId(event.getOrderId())) {
				return;
			}
			Inventory inventory =  inventoryRepo.findBySkuCode(event.getSkuCode()).orElseThrow(()-> new InventoryNotFoundException(event.getSkuCode()));
		Integer availableQuantity =inventory.getQuantity();
		Integer orderedQuantity = event.getQuantity();
		if(availableQuantity<orderedQuantity) {
			throw new RuntimeException("Insufficient stock for this skuCode");
			
		}
		inventory.setQuantity(availableQuantity-orderedQuantity);
		inventoryRepo.save(inventory);
		ProcessedOrder processed = new ProcessedOrder();
		processed.setOrderId(event.getOrderId());
		processed.setProcessedAt(LocalDateTime.now());
		processedOrderRepo.save(processed);
		}
}




