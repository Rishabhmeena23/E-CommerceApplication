package com.ecommerce.admin_service.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.admin_service.client.AuthServiceClient;
import com.ecommerce.admin_service.client.OrderServiceClient;
import com.ecommerce.admin_service.client.ProductServiceClient;
import com.ecommerce.admin_service.dto.BanUserRequest;
import com.ecommerce.admin_service.dto.DashboardDto;
import com.ecommerce.admin_service.dto.OrderDto;
import com.ecommerce.admin_service.dto.ProductDto;
import com.ecommerce.admin_service.dto.RoleRequest;
import com.ecommerce.admin_service.dto.UserDto;
import com.ecommerce.admin_service.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    private final AuthServiceClient authServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;

    public AdminServiceImpl(
            AuthServiceClient authServiceClient,
            ProductServiceClient productServiceClient,
            OrderServiceClient orderServiceClient) {

        this.authServiceClient = authServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public DashboardDto getDashboard() {

        long totalUsers = authServiceClient.getAllUsers().size();
        long totalProducts = productServiceClient.getAllProducts().size();
        long totalOrders = orderServiceClient.getAllOrders().size();

        long pendingDisputes = 0;

        return new DashboardDto(
                totalUsers,
                totalProducts,
                totalOrders,
                pendingDisputes);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return authServiceClient.getAllUsers();
    }

    @Override
    public UserDto getUserById(Long id) {
        return authServiceClient.getUserById(id);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productServiceClient.getAllProducts();
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderServiceClient.getAllOrders();
    }

    @Override
    public void banUser(Long id, BanUserRequest request) {
        authServiceClient.banUser(id, request.getReason());
    }

    @Override
    public void unbanUser(Long id) {
        authServiceClient.unbanUser(id);
    }

    @Override
    public void assignRole(Long id, RoleRequest request) {
        authServiceClient.assignRole(id, request.getRole());
    }

}