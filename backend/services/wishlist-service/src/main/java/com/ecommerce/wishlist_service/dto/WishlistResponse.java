package com.ecommerce.wishlist_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {

    private Long wishlistId;

    private Long userId;

    private List<WishlistItemResponse> items;

}
