package com.ecommerce.cart_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

}