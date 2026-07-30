package com.ecommerce.customer_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.customer_service.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
	
	Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
	
	boolean existsByEmail(String email);
}
