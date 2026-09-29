package com.ecommerce.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.payment.service.RazorpayWebhookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class WebhookController {
private final RazorpayWebhookService webhookService;

@PostMapping("/webhook/razorpay")
public ResponseEntity<Void> handleWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload){
	webhookService.processWebhook(signature, payload);
	return ResponseEntity.ok().build();
}
}
