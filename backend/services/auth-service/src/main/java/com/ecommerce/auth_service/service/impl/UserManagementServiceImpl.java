package com.ecommerce.auth_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.service.UserManagementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {

        User user = findUser(id);

        return toDto(user);
    }

    @Override
    public UserDto banUser(Long id) {

        User user = findUser(id);

        user.setActive(false);

        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto unbanUser(Long id) {

        User user = findUser(id);

        user.setActive(true);

        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto changeUserRole(Long id, String role) {

        User user = findUser(id);

        Role newRole;

        try {
            newRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Invalid role. Allowed roles: CUSTOMER, SELLER, ADMIN"
            );
        }

        user.setRole(newRole);

        return toDto(userRepository.save(user));
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    private UserDto toDto(User user) {

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}