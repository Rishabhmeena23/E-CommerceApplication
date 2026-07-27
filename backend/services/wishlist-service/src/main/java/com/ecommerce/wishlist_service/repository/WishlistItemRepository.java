package com.ecommerce.wishlist_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.wishlist_service.entity.WishlistItem;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistWishlistIdAndProductId(
            Long wishlistId,
            Long productId);

    void deleteByWishlistWishlistIdAndProductId(
            Long wishlistId,
            Long productId);

}