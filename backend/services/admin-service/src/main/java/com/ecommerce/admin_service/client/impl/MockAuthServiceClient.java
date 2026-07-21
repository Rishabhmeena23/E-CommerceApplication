package com.ecommerce.admin_service.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.admin_service.client.AuthServiceClient;
import com.ecommerce.admin_service.dto.UserDto;

@Service
public class MockAuthServiceClient implements AuthServiceClient {

    private final List<UserDto> users = new ArrayList<>();

    public MockAuthServiceClient() {

        users.add(new UserDto(
                1L,
                "John",
                "Doe",
                "john@test.com",
                "ROLE_USER",
                "ACTIVE"));

        users.add(new UserDto(
                2L,
                "Alice",
                "Smith",
                "alice@test.com",
                "ROLE_ADMIN",
                "ACTIVE"));

        users.add(new UserDto(
                3L,
                "Bob",
                "Brown",
                "bob@test.com",
                "ROLE_USER",
                "BANNED"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users;
    }

    @Override
    public UserDto getUserById(Long id) {

        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void banUser(Long id, String reason) {

        UserDto user = getUserById(id);

        if (user != null) {
            user.setStatus("BANNED");
        }
    }

    @Override
    public void unbanUser(Long id) {

        UserDto user = getUserById(id);

        if (user != null) {
            user.setStatus("ACTIVE");
        }
    }

    @Override
    public void assignRole(Long id, String role) {

        UserDto user = getUserById(id);

        if (user != null) {
            user.setRole(role);
        }
    }

}