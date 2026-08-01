package com.ecommerce.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long userId, String customerEmail, String orderStatus,
        BigDecimal totalAmount, String shippingAddress, String paymentReference,
        List<OrderItemResponse> items, LocalDateTime createdAt, LocalDateTime updatedAt) { }
