package com.ecommerce.payment.DTO;

import java.math.BigDecimal;

import com.ecommerce.payment.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPayment {
		  @NotNull String orderId;
		@Positive private BigDecimal amount;
		@NotNull private PaymentMethod paymentMethod;
}
