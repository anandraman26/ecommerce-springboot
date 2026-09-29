package com.ecommerce.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    @NotBlank(message = "SKU code must not be blank")
    private String skuCode;
    @Min(value = 0, message = "Quantity must be 0 or greater")
    private Integer quantity;
}
