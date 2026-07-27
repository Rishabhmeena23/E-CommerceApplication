package com.ecommerce.wishlist_service.service;

import com.ecommerce.wishlist_service.dto.AddToWishlistRequest;
import com.ecommerce.wishlist_service.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse createWishlist(Long customerId);

    WishlistResponse getWishlist(Long customerId);

    WishlistResponse addToWishlist(Long customerId,
                                   AddToWishlistRequest request);

    void removeFromWishlist(Long customerId,
                            Long productId);

    void clearWishlist(Long customerId);

}