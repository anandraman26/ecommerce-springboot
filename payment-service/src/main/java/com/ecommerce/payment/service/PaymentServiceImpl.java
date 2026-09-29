package com.ecommerce.payment.service;

import com.ecommerce.payment.DTO.PaymentResponse;
import com.ecommerce.payment.DTO.PaymentStatusUpdateRequest;
import com.ecommerce.payment.DTO.RequestPayment;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.enums.PaymentStatus;
import com.ecommerce.payment.exception.PaymentNotFoundException;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.util.TransactionGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;

    @Override
    public PaymentResponse createPayment(RequestPayment request) {
        Payment payment = Payment.builder().orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(TransactionGenerator.generate()).build();
        Payment savedPayment = paymentRepo.save(payment);
        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepo.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found for this orderid " + orderId));
        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long PaymentId, PaymentStatusUpdateRequest request) {
        // TODO Auto-generated method stub
        Payment payment = paymentRepo.findById(PaymentId).orElseThrow(() -> new PaymentNotFoundException("payment not found with id " + PaymentId));
        payment.setPaymentStatus(request.getPaymentStatus());
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getTransactionId());
    }
}
