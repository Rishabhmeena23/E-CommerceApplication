package com.ecommerce.wishlist_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.wishlist_service.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);

}