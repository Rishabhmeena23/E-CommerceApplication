package com.ecommerce.auth_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.auth_service.dto.RoleRequest;
import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.service.UserManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(
            UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {

        return ResponseEntity.ok(
                userManagementService.getAllUsers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userManagementService.getUserById(id)
        );
    }

    @PatchMapping("/{id}/ban")
    public ResponseEntity<UserDto> banUser(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userManagementService.banUser(id)
        );
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<UserDto> unbanUser(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userManagementService.unbanUser(id)
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDto> changeRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {

        return ResponseEntity.ok(
                userManagementService.changeUserRole(
                        id,
                        request.getRole()
                )
        );
    }
}