package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.AddToCartRequest;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.dto.UpdateQuantityRequest;

public interface CartService {

    CartResponse createCart(Long userId);

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId,
                           AddToCartRequest request);

    CartResponse updateQuantity(Long userId,
                                Long productId,
                                UpdateQuantityRequest request);

    void removeItem(Long userId,
                    Long productId);

    void clearCart(Long userId);

}
