package com.ecommerce.payment.service;

import com.ecommerce.payment.DTO.GatewayOrderResponse;

import java.math.BigDecimal;

public interface PaymentGateway {
    GatewayOrderResponse createOrder(String orderId, BigDecimal amount);
}
