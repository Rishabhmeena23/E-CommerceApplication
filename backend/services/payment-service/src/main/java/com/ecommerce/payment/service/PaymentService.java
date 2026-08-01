package com.ecommerce.payment.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;
    private final OrderClient orders;

    @Transactional
    public PaymentResponse pay(Long userId, PaymentRequest request) {
        OrderClient.OrderSnapshot order = orders.get(request.orderId());
        if (order == null || !order.userId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This order belongs to another user");
        if ("PAID".equals(order.orderStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "This order is already paid");
        if ("CANCELLED".equals(order.orderStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT, "This order is cancelled");
        repository.findFirstByOrderIdAndStatus(order.id(), PaymentStatus.SUCCESS).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A successful payment already exists");
        });
        String method = request.paymentMethod().trim().toUpperCase(Locale.ROOT);
        if (!List.of("CARD", "UPI").contains(method)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use CARD or UPI");
        String digits = request.cardNumber() == null ? "" : request.cardNumber().replaceAll("\\s", "");
        if ("CARD".equals(method) && digits.length() < 12) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid dummy card number");
        boolean declined = "CARD".equals(method) && digits.endsWith("0000");
        Payment payment = new Payment();
        payment.setPaymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase(Locale.ROOT));
        payment.setOrderId(order.id()); payment.setUserId(userId); payment.setAmount(order.totalAmount()); payment.setPaymentMethod(method);
        payment.setLastFour(digits.length() >= 4 ? digits.substring(digits.length() - 4) : null);
        payment.setStatus(declined ? PaymentStatus.FAILED : PaymentStatus.SUCCESS);
        payment.setFailureReason(declined ? "Dummy bank declined this card" : null);
        Payment saved = repository.save(payment);
        orders.updatePayment(order.id(), saved.getPaymentReference(), saved.getStatus().name());
        return response(saved);
    }
    @Transactional(readOnly = true) public List<PaymentResponse> mine(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::response).toList();
    }
    @Transactional(readOnly = true) public List<PaymentResponse> all() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::response).toList();
    }
    @Transactional(readOnly = true) public PaymentResponse get(Long id, Long userId, String role) {
        Payment payment = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (!payment.getUserId().equals(userId) && !"ADMIN".equals(role)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This payment belongs to another user");
        return response(payment);
    }
    private PaymentResponse response(Payment value) { return new PaymentResponse(value.getId(), value.getPaymentReference(),
            value.getOrderId(), value.getUserId(), value.getAmount(), value.getPaymentMethod(), value.getLastFour(),
            value.getStatus().name(), value.getFailureReason(), value.getCreatedAt()); }
}
