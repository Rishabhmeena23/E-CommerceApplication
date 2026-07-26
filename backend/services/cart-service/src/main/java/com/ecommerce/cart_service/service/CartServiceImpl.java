package com.ecommerce.cart_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.cart_service.dto.AddToCartRequest;
import com.ecommerce.cart_service.dto.CartItemResponse;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.dto.UpdateQuantityRequest;
import com.ecommerce.cart_service.entity.Cart;
import com.ecommerce.cart_service.entity.CartItem;
import com.ecommerce.cart_service.exception.CartNotFoundException;
import com.ecommerce.cart_service.exception.ProductNotFoundException;
import com.ecommerce.cart_service.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public CartResponse createCart(Long customerId) {

        if (cartRepository.existsByCustomerId(customerId)) {
            throw new RuntimeException("Cart already exists");
        }

        Cart cart = Cart.builder()
                .customerId(customerId)
                .build();

        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCart(Long customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        return mapToResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long customerId,
                                  AddToCartRequest request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .customerId(customerId)
                                        .build()));

        CartItem existingItem = cart.getCartItems()
                .stream()
                .filter(item ->
                        item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + request.getQuantity());

        } else {

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())

                    // Replace with Product Service price later
                    .price(BigDecimal.ZERO)
                    .build();

            cart.getCartItems().add(item);
        }

        Cart updatedCart = cartRepository.save(cart);

        return mapToResponse(updatedCart);
    }

    @Override
    public CartResponse updateQuantity(Long customerId,
                                       Long productId,
                                       UpdateQuantityRequest request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        CartItem item = cart.getCartItems()
                .stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found in cart"));

        item.setQuantity(request.getQuantity());

        Cart updatedCart = cartRepository.save(cart);

        return mapToResponse(updatedCart);
    }

    @Override
    public void removeItem(Long customerId,
                           Long productId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        boolean removed = cart.getCartItems()
                .removeIf(item ->
                        item.getProductId().equals(productId));

        if (!removed) {
            throw new ProductNotFoundException("Product not found in cart");
        }

        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        cart.getCartItems().clear();

        cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {

        List<CartItemResponse> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {

            BigDecimal subtotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(subtotal);

            items.add(
                    CartItemResponse.builder()
                            .cartItemId(item.getCartItemId())
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .subtotal(subtotal)
                            .build()
            );
        }

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .customerId(cart.getCustomerId())
                .items(items)
                .totalAmount(total)
                .build();
    }
}