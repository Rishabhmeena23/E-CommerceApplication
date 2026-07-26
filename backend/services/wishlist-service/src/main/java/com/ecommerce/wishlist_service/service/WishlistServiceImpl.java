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
    public WishlistResponse createWishlist(Long customerId) {

        if (wishlistRepository.existsByCustomerId(customerId)) {
            throw new RuntimeException("Wishlist already exists");
        }

        Wishlist wishlist = Wishlist.builder()
                .customerId(customerId)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    @Override
    public WishlistResponse getWishlist(Long customerId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new WishlistNotFoundException("Wishlist not found"));

        return mapToResponse(wishlist);
    }

    @Override
    public WishlistResponse addToWishlist(Long customerId,
                                          AddToWishlistRequest request) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
                .orElseGet(() ->
                        wishlistRepository.save(
                                Wishlist.builder()
                                        .customerId(customerId)
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
    public void removeFromWishlist(Long customerId,
                                   Long productId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
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
    public void clearWishlist(Long customerId) {

        Wishlist wishlist = wishlistRepository.findByCustomerId(customerId)
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
                .customerId(wishlist.getCustomerId())
                .items(itemResponses)
                .build();
    }
}