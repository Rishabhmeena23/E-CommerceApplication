package com.ecommerce.payment.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.security.AuthenticatedUser;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/payments") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService payments;
    @PostMapping public ResponseEntity<PaymentResponse> pay(@AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payments.pay(user.userId(), request));
    }
    @GetMapping("/me") public List<PaymentResponse> mine(@AuthenticationPrincipal AuthenticatedUser user) { return payments.mine(user.userId()); }
    @GetMapping public List<PaymentResponse> all() { return payments.all(); }
    @GetMapping("/{id}") public PaymentResponse get(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        return payments.get(id, user.userId(), user.role());
    }
}
