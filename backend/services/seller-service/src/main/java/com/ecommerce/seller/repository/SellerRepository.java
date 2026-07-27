package com.ecommerce.seller.repository;

import com.ecommerce.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    // Existing methods
    Optional<Seller> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByGstNumberAndSellerIdNot(String gstNumber, Long sellerId);

    // Soft Delete methods
    Optional<Seller> findBySellerIdAndActiveTrue(Long sellerId);

    Optional<Seller> findByUserIdAndActiveTrue(Long userId);

    List<Seller> findByActiveTrue();
}