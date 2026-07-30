package com.ecommerce.seller.service;

import com.ecommerce.seller.dto.request.CreateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerStatusRequest;
import com.ecommerce.seller.dto.response.SellerResponse;

import java.util.List;

public interface SellerService {

    SellerResponse createSeller(Long userId, CreateSellerRequest request);

    SellerResponse getSellerById(Long sellerId);

    SellerResponse getSellerByUserId(Long userId);

    List<SellerResponse> getAllSellers();

    SellerResponse updateSeller(Long sellerId, Long requesterUserId, UpdateSellerRequest request);

    SellerResponse updateSellerStatus(
            Long sellerId,
            UpdateSellerStatusRequest request
    );

    // Soft Delete
    void deactivateSeller(Long sellerId, Long requesterUserId);
}
