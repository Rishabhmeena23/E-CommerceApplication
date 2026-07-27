package com.ecommerce.seller.dto.response;

import com.ecommerce.seller.entity.enums.ApprovalStatus;
import com.ecommerce.seller.entity.enums.SellerType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SellerResponse {

    private Long sellerId;
    private Long userId;
    private String shopName;
    private String shopDescription;
    private String phone;
    private String gstNumber;
    private SellerType sellerType;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}