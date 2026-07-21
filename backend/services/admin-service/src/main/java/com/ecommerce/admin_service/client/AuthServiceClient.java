package com.ecommerce.admin_service.client;

import java.util.List;

import com.ecommerce.admin_service.dto.UserDto;

public interface AuthServiceClient {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    void banUser(Long id, String reason);

    void unbanUser(Long id);

    void assignRole(Long id, String role);

}
