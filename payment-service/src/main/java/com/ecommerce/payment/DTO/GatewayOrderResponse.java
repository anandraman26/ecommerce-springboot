package com.ecommerce.payment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayOrderResponse {
		private String gatewayOrderId;
		private String currency;
		private BigDecimal amount;
		private String status;
		private String gatewayName;
}
