package com.ecommerce.payment.service;

import com.ecommerce.payment.client.InventoryClient;
import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.enums.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookService {
    private final PaymentRepository paymentRepo;
    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @Transactional
    public void processWebhook(String signature, String payload) {
        verifySignature(signature, payload);

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");
        log.info("Received Razorpay webhook event: {}", eventType);

        switch (eventType) {
            case "payment.captured" -> handlePaymentSuccess(event);
            case "payment.failed" -> handlePaymentFailure(event);
            default -> log.warn("Unhandled Razorpay event: {}", eventType);
        }
    }

    private void handlePaymentSuccess(JSONObject event) {
        JSONObject paymentEntity = extractPaymentEntity(event);
        String razorpayOrderId = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");

        Payment payment = paymentRepo.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found for Razorpay orderId: " + razorpayOrderId));

        // Razorpay can retry the same webhook. Replaying this is safe because
        // the downstream confirm/commit operations are idempotent.
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setGatewayPaymentId(razorpayPaymentId);
        paymentRepo.save(payment);

        orderClient.confirmOrder(payment.getOrderId());
        inventoryClient.commitInventory(payment.getOrderId());
    }

    private void handlePaymentFailure(JSONObject event) {
        JSONObject paymentEntity = extractPaymentEntity(event);
        String razorpayOrderId = paymentEntity.getString("order_id");

        Payment payment = paymentRepo.findByTransactionId(razorpayOrderId)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found for this Razorpay order id: " + razorpayOrderId));

        payment.setPaymentStatus(PaymentStatus.FAILD);
        paymentRepo.save(payment);

        // Rollback first so stock is released before the order is marked failed.
        inventoryClient.rollbackInventory(payment.getOrderId());
        orderClient.failOrder(payment.getOrderId());
    }

    private JSONObject extractPaymentEntity(JSONObject event) {
        return event.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");
    }

    private void verifySignature(String signature, String payload) {
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (Exception e) {
            log.error("Invalid Razorpay webhook signature", e);
            throw new SecurityException("Invalid Razorpay webhook signature");
        }
    }
}
