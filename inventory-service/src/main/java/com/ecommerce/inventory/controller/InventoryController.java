package com.ecommerce.inventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
		private final InventoryService inventoryService;
		@GetMapping("/{skuCode}")
		public ResponseEntity<InventoryResponse> isInStock(@PathVariable String skuCode){
			return ResponseEntity.ok(inventoryService.checkInventory(skuCode));
		}
		@PostMapping
		public ResponseEntity<String> addInventory(@RequestBody InventoryRequest request){
			inventoryService.addInventory(request);
			return ResponseEntity.status(HttpStatus.CREATED).body("Inventory added successfully");
		}
}
