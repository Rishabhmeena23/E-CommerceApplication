package com.ecommerce.customer_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.customer_service.dto.CustomerRequest;
import com.ecommerce.customer_service.dto.CustomerResponse;
import com.ecommerce.customer_service.security.AuthenticatedUser;
import com.ecommerce.customer_service.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CustomerRequest request) {

        return new ResponseEntity<>(
                service.createCustomer(currentUser.userId(), currentUser.email(), request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getCurrentCustomer(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(service.getCurrentCustomer(currentUser.userId()));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerResponse> updateCurrentCustomer(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(service.updateCurrentCustomer(currentUser.userId(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentCustomer(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        service.deleteCurrentCustomer(currentUser.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(service.getAllCustomers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {

        return ResponseEntity.ok(service.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}
