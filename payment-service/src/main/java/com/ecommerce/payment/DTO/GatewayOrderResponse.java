package com.ecommerce.payment.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
