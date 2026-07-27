package com.ecommerce.auth_service.dto;

import jakarta.validation.constraints.NotNull;

public class RoleRequest {

    @NotNull(message = "Role is required")
    private String role;

    public RoleRequest() {
    }

    public RoleRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}