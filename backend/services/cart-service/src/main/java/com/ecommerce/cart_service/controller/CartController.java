package com.ecommerce.cart_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.cart_service.dto.AddToCartRequest;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.dto.UpdateQuantityRequest;
import com.ecommerce.cart_service.service.CartService;
import com.ecommerce.cart_service.security.AuthenticatedUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return new ResponseEntity<>(
                cartService.createCart(currentUser.userId()),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return ResponseEntity.ok(
                cartService.getCart(currentUser.userId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(currentUser.userId(), request));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        currentUser.userId(),
                        productId,
                        request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeItem(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long productId) {

        cartService.removeItem(currentUser.userId(), productId);

        return ResponseEntity.ok("Item removed successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        cartService.clearCart(currentUser.userId());

        return ResponseEntity.ok("Cart cleared successfully");
    }

}
