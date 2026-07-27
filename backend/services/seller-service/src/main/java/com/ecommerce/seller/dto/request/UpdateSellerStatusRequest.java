package com.ecommerce.seller.dto.request;

import com.ecommerce.seller.entity.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSellerStatusRequest {

    @NotNull(message = "Approval status is required")
    private ApprovalStatus approvalStatus;
}