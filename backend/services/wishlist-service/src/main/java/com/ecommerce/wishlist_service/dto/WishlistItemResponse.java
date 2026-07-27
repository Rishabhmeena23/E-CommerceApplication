package com.ecommerce.wishlist_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistItemResponse {

    private Long wishlistItemId;

    private Long productId;

}