package com.ecommerce.auth_service.service;

import java.util.List;

import com.ecommerce.auth_service.dto.UserDto;

public interface UserManagementService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto banUser(Long id);

    UserDto unbanUser(Long id);

    UserDto changeUserRole(Long id, String role);
}