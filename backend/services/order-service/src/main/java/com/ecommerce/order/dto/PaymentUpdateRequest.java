package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentUpdateRequest(@NotBlank String paymentReference, @NotBlank String paymentStatus) { }
