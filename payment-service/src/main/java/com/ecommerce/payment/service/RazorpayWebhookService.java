package com.ecommerce.payment.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.payment.client.InventoryClient;
import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.enums.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayWebhookService {
		private final PaymentRepository paymentRepo;
		private final OrderClient orderClient;
		private final InventoryClient inventoryClient;
		@Value("${razorpay.webhook-secret}")
		private String webhookSecret;
		
		public void processWebhook(String signature, String payload) {
			verifySignature(signature,payload);
			JSONObject event = new JSONObject(payload);
			String eventType = event.getString("event");
			log.info("Received RazorpayWebhook event ", eventType);
			
			switch (eventType) {
			case "payment.captured" -> handlePaymentSuccess(event);
			case "payment.failed" -> handlePaymentFailure(event);
			default -> log.warn("unhandled razorpay event ", eventType);
			}
		}
		
		private void handlePaymentSuccess(JSONObject event) {
			JSONObject paymentEntity = extractPaymentEntity(event);
			String razorpayOrderId = paymentEntity.getString("order_id");
			String razorpayPaymentId = paymentEntity.getString("id");
			Payment payment =  paymentRepo.findByTransactionId(razorpayOrderId).orElseThrow(() -> new IllegalStateException("Payment not found for Razorpay orderId"));
		payment.setPaymentStatus(PaymentStatus.SUCCESS);
		payment.setGatewayPaymentId(razorpayPaymentId);
		orderClient.confirmOrder(payment.getOrderId());
		inventoryClient.commitInventory(payment.getOrderId());
		}
		private void handlePaymentFailure(JSONObject event) {
			JSONObject paymentEntity =  extractPaymentEntity(event);
			String razorpayOrderId =  paymentEntity.getString("order_id");
			Payment payment =  paymentRepo.findByTransactionId(razorpayOrderId).orElseThrow(()-> new IllegalStateException("Payment not found for this razorpay order id"));
		payment.setPaymentStatus(PaymentStatus.FAILD);
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
				// TODO: handle exception
				log.error("invalid razorpay webhook signature");
				throw new SecurityException("Invalid razorpay webhook signature");
			}
		}
		
		
		
		
		
		
		
		
		
		
}
