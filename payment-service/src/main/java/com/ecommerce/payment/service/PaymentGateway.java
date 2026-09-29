package com.ecommerce.payment.service;

import java.math.BigDecimal;

import com.ecommerce.payment.DTO.GatewayOrderResponse;

public interface PaymentGateway {
GatewayOrderResponse createOrder(String orderId, BigDecimal amount);
}
