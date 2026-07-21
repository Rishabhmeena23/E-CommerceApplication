package com.ecommerce.admin_service.dto;


import jakarta.validation.constraints.NotBlank;

public class BanUserRequest {

    @NotBlank(message = "Reason is required")
    private String reason;

    public BanUserRequest() {
    }

    public BanUserRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
