package com.ecommerce.admin_service.dto;


import jakarta.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank(message = "Role is required")
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