package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.AddToCartRequest;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.dto.UpdateQuantityRequest;

public interface CartService {

    CartResponse createCart(Long customerId);

    CartResponse getCart(Long customerId);

    CartResponse addToCart(Long customerId,
                           AddToCartRequest request);

    CartResponse updateQuantity(Long customerId,
                                Long productId,
                                UpdateQuantityRequest request);

    void removeItem(Long customerId,
                    Long productId);

    void clearCart(Long customerId);

}