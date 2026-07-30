package com.ecommerce.wishlist_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.wishlist_service.dto.AddToWishlistRequest;
import com.ecommerce.wishlist_service.dto.WishlistResponse;
import com.ecommerce.wishlist_service.service.WishlistService;
import com.ecommerce.wishlist_service.security.AuthenticatedUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistResponse> createWishlist(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return new ResponseEntity<>(
                wishlistService.createWishlist(currentUser.userId()),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(currentUser.userId()));
    }

    @PostMapping("/items")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody AddToWishlistRequest request) {

        return ResponseEntity.ok(
                wishlistService.addToWishlist(currentUser.userId(), request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeProduct(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(currentUser.userId(), productId);

        return ResponseEntity.ok("Product removed successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> clearWishlist(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        wishlistService.clearWishlist(currentUser.userId());

        return ResponseEntity.ok("Wishlist cleared successfully");
    }
}
