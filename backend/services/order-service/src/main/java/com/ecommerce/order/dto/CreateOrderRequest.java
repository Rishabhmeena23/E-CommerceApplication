package com.ecommerce.order.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotEmpty List<@Valid OrderItemRequest> items,
        @NotBlank @Size(max = 500) String shippingAddress) { }
