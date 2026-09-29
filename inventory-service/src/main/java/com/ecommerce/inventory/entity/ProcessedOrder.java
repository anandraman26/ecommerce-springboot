package com.ecommerce.inventory.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_order", uniqueConstraints = @UniqueConstraint(columnNames = "orderId"))
public class ProcessedOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
	@Column(nullable = false, unique = true)
		private String orderId;
		private LocalDateTime processedAt;
}
