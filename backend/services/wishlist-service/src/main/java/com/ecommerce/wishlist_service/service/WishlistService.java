package com.ecommerce.wishlist_service.service;

import com.ecommerce.wishlist_service.dto.AddToWishlistRequest;
import com.ecommerce.wishlist_service.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse createWishlist(Long userId);

    WishlistResponse getWishlist(Long userId);

    WishlistResponse addToWishlist(Long userId,
                                   AddToWishlistRequest request);

    void removeFromWishlist(Long userId,
                            Long productId);

    void clearWishlist(Long userId);

}
