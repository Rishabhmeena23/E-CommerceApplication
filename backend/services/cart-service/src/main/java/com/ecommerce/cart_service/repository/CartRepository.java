package com.ecommerce.cart_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.cart_service.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);

}