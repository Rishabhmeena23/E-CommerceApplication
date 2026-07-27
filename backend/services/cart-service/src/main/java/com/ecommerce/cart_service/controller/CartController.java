package com.ecommerce.cart_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.cart_service.dto.AddToCartRequest;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.dto.UpdateQuantityRequest;
import com.ecommerce.cart_service.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{customerId}")
    public ResponseEntity<CartResponse> createCart(
            @PathVariable Long customerId) {

        return new ResponseEntity<>(
                cartService.createCart(customerId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                cartService.getCart(customerId));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long customerId,
            @Valid @RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(customerId, request));
    }

    @PutMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        customerId,
                        productId,
                        request));
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long customerId,
            @PathVariable Long productId) {

        cartService.removeItem(customerId, productId);

        return ResponseEntity.ok("Item removed successfully");
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> clearCart(
            @PathVariable Long customerId) {

        cartService.clearCart(customerId);

        return ResponseEntity.ok("Cart cleared successfully");
    }

}