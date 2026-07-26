package com.ecommerce.wishlist_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToWishlistRequest {

    @NotNull(message = "Product Id is required")
    private Long productId;

}