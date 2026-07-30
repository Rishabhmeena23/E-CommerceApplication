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
import com.ecommerce.cart_service.client.ProductClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    @Override
    public CartResponse createCart(Long userId) {

        if (cartRepository.existsByUserId(userId)) {
            throw new RuntimeException("Cart already exists");
        }

        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        return mapToResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long userId,
                                  AddToCartRequest request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .userId(userId)
                                        .build()));

        ProductClient.ProductSnapshot product = productClient.getProduct(request.getProductId());
        int requestedQuantity = request.getQuantity();

        CartItem existingItem = cart.getCartItems()
                .stream()
                .filter(item ->
                        item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            requestedQuantity += existingItem.getQuantity();
            validateAvailableStock(product, requestedQuantity);

            existingItem.setQuantity(
                    requestedQuantity);

        } else {

            validateAvailableStock(product, requestedQuantity);

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())

                    .price(product.price())
                    .build();

            cart.getCartItems().add(item);
        }

        Cart updatedCart = cartRepository.save(cart);

        return mapToResponse(updatedCart);
    }

    @Override
    public CartResponse updateQuantity(Long userId,
                                       Long productId,
                                       UpdateQuantityRequest request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        CartItem item = cart.getCartItems()
                .stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found in cart"));

        ProductClient.ProductSnapshot product = productClient.getProduct(productId);
        validateAvailableStock(product, request.getQuantity());
        item.setQuantity(request.getQuantity());

        Cart updatedCart = cartRepository.save(cart);

        return mapToResponse(updatedCart);
    }

    @Override
    public void removeItem(Long userId,
                           Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
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
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
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
                .userId(cart.getUserId())
                .items(items)
                .totalAmount(total)
                .build();
    }

    private void validateAvailableStock(ProductClient.ProductSnapshot product, int quantity) {
        if (product == null || product.price() == null || product.inventory() == null
                || product.inventory().availableQuantity() < quantity) {
            throw new ProductNotFoundException("Product is unavailable or does not have enough stock");
        }
    }
}
