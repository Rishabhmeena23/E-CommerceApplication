package com.ecommerce.admin_service.service;

import java.util.List;

import com.ecommerce.admin_service.dto.BanUserRequest;
import com.ecommerce.admin_service.dto.DashboardDto;
import com.ecommerce.admin_service.dto.OrderDto;
import com.ecommerce.admin_service.dto.ProductDto;
import com.ecommerce.admin_service.dto.RoleRequest;
import com.ecommerce.admin_service.dto.UserDto;

public interface AdminService {

    DashboardDto getDashboard();

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    List<ProductDto> getAllProducts();

    List<OrderDto> getAllOrders();

    void banUser(Long id, BanUserRequest request);

    void unbanUser(Long id);

    void assignRole(Long id, RoleRequest request);

}