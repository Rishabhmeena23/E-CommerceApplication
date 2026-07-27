package com.ecommerce.wishlist_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.wishlist_service.dto.AddToWishlistRequest;
import com.ecommerce.wishlist_service.dto.WishlistResponse;
import com.ecommerce.wishlist_service.service.WishlistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{customerId}")
    public ResponseEntity<WishlistResponse> createWishlist(
            @PathVariable Long customerId) {

        return new ResponseEntity<>(
                wishlistService.createWishlist(customerId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<WishlistResponse> getWishlist(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(customerId));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long customerId,
            @Valid @RequestBody AddToWishlistRequest request) {

        return ResponseEntity.ok(
                wishlistService.addToWishlist(customerId, request));
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<String> removeProduct(
            @PathVariable Long customerId,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(customerId, productId);

        return ResponseEntity.ok("Product removed successfully");
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> clearWishlist(
            @PathVariable Long customerId) {

        wishlistService.clearWishlist(customerId);

        return ResponseEntity.ok("Wishlist cleared successfully");
    }
}