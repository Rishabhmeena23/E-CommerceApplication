package com.ecommerce.wishlist_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.wishlist_service.dto.AddToWishlistRequest;
import com.ecommerce.wishlist_service.dto.WishlistItemResponse;
import com.ecommerce.wishlist_service.dto.WishlistResponse;
import com.ecommerce.wishlist_service.entity.Wishlist;
import com.ecommerce.wishlist_service.entity.WishlistItem;
import com.ecommerce.wishlist_service.exception.ProductAlreadyExistsException;
import com.ecommerce.wishlist_service.exception.ProductNotFoundException;
import com.ecommerce.wishlist_service.exception.WishlistNotFoundException;
import com.ecommerce.wishlist_service.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public WishlistResponse createWishlist(Long userId) {

        if (wishlistRepository.existsByUserId(userId)) {
            throw new RuntimeException("Wishlist already exists");
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    @Override
    public WishlistResponse getWishlist(Long userId) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new WishlistNotFoundException("Wishlist not found"));

        return mapToResponse(wishlist);
    }

    @Override
    public WishlistResponse addToWishlist(Long userId,
                                          AddToWishlistRequest request) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() ->
                        wishlistRepository.save(
                                Wishlist.builder()
                                        .userId(userId)
                                        .build()));

        boolean exists = wishlist.getWishlistItems()
                .stream()
                .anyMatch(item ->
                        item.getProductId().equals(request.getProductId()));

        if (exists) {
            throw new ProductAlreadyExistsException(
                    "Product already exists in wishlist");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlist(wishlist)
                .productId(request.getProductId())
                .build();

        wishlist.getWishlistItems().add(wishlistItem);

        Wishlist updatedWishlist = wishlistRepository.save(wishlist);

        return mapToResponse(updatedWishlist);
    }

    @Override
    public void removeFromWishlist(Long userId,
                                   Long productId) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new WishlistNotFoundException("Wishlist not found"));

        boolean removed = wishlist.getWishlistItems()
                .removeIf(item ->
                        item.getProductId().equals(productId));

        if (!removed) {
            throw new ProductNotFoundException(
                    "Product not found in wishlist");
        }

        wishlistRepository.save(wishlist);
    }

    @Override
    public void clearWishlist(Long userId) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new WishlistNotFoundException("Wishlist not found"));

        wishlist.getWishlistItems().clear();

        wishlistRepository.save(wishlist);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {

        List<WishlistItemResponse> itemResponses = new ArrayList<>();

        for (WishlistItem item : wishlist.getWishlistItems()) {

            WishlistItemResponse response = WishlistItemResponse.builder()
                    .wishlistItemId(item.getWishlistItemId())
                    .productId(item.getProductId())
                    .build();

            itemResponses.add(response);
        }

        return WishlistResponse.builder()
                .wishlistId(wishlist.getWishlistId())
                .userId(wishlist.getUserId())
                .items(itemResponses)
                .build();
    }
}
