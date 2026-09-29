package com.ecommerce.payment.service;

import com.ecommerce.payment.DTO.GatewayOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RozarpayPaymentGateway implements PaymentGateway {

    private final RazorpayClient razorpayClient;

    @Override
    public GatewayOrderResponse createOrder(String orderId, BigDecimal amount) {

        try {
            JSONObject options = new JSONObject();
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
            options.put("currency", "INR");
            options.put("receipt", orderId);
            Order order = razorpayClient.orders.create(options);
            return new GatewayOrderResponse(
                    order.get("id"),
                    order.get("currency"),
                    amount,
                    order.get("status"),
                    "RAZORPAY");
        } catch (RazorpayException e) {
            // TODO: handle exception
            throw new IllegalStateException("RazorpayOrder Create failed");
        }
    }
}
