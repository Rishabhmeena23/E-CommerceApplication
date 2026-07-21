package com.ecommerce.admin_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.admin_service.dto.ApiResponse;
import com.ecommerce.admin_service.dto.BanUserRequest;
import com.ecommerce.admin_service.dto.DashboardDto;
import com.ecommerce.admin_service.dto.OrderDto;
import com.ecommerce.admin_service.dto.ProductDto;
import com.ecommerce.admin_service.dto.RoleRequest;
import com.ecommerce.admin_service.dto.UserDto;
import com.ecommerce.admin_service.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {

        return ResponseEntity.ok(
                new ApiResponse(true, "Admin Service is running"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {

        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {

        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse> banUser(
            @PathVariable Long id,
            @Valid @RequestBody BanUserRequest request) {

        adminService.banUser(id, request);

        return ResponseEntity.ok(
                new ApiResponse(true, "User banned successfully"));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<ApiResponse> unbanUser(
            @PathVariable Long id) {

        adminService.unbanUser(id);

        return ResponseEntity.ok(
                new ApiResponse(true, "User unbanned successfully"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {

        adminService.assignRole(id, request);

        return ResponseEntity.ok(
                new ApiResponse(true, "Role assigned successfully"));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {

        return ResponseEntity.ok(adminService.getAllOrders());
    }

}