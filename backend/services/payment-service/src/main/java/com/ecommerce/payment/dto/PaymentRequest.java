package com.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
        @NotNull Long orderId,
        @NotBlank String paymentMethod,
        @Pattern(regexp = "^$|^[0-9 ]{12,23}$", message = "Card number must contain 12 to 19 digits") String cardNumber) { }
