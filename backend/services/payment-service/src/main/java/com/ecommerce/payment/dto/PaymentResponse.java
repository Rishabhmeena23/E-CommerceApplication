package com.ecommerce.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Long id, String paymentReference, Long orderId, Long userId,
        BigDecimal amount, String paymentMethod, String lastFour, String status,
        String failureReason, LocalDateTime createdAt) { }
