package com.ecommerce.seller.service.impl;

import com.ecommerce.seller.dto.request.CreateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerStatusRequest;
import com.ecommerce.seller.dto.response.SellerResponse;
import com.ecommerce.seller.entity.Seller;
import com.ecommerce.seller.entity.enums.ApprovalStatus;
import com.ecommerce.seller.exception.ResourceAlreadyExistsException;
import com.ecommerce.seller.exception.ResourceNotFoundException;
import com.ecommerce.seller.repository.SellerRepository;
import com.ecommerce.seller.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;

    @Override
    public SellerResponse createSeller(CreateSellerRequest request) {

        if (sellerRepository.existsByUserId(request.getUserId())) {
            throw new ResourceAlreadyExistsException(
                    "Seller already exists for user ID: " + request.getUserId()
            );
        }

        if (request.getGstNumber() != null
                && sellerRepository.existsByGstNumber(request.getGstNumber())) {
            throw new ResourceAlreadyExistsException(
                    "Seller already exists for this GST number"
            );
        }

        Seller seller = Seller.builder()
                .userId(request.getUserId())
                .shopName(request.getShopName())
                .shopDescription(request.getShopDescription())
                .phone(request.getPhone())
                .gstNumber(request.getGstNumber())
                .sellerType(request.getSellerType())
                .approvalStatus(ApprovalStatus.PENDING)
                .build();

        return mapToResponse(sellerRepository.save(seller));
    }

    @Override
    public SellerResponse getSellerById(Long sellerId) {
        return mapToResponse(findSellerById(sellerId));
    }

    @Override
    public SellerResponse getSellerByUserId(Long userId) {
        Seller seller = sellerRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found for user ID: " + userId
                ));

        return mapToResponse(seller);
    }

    @Override
    public List<SellerResponse> getAllSellers() {
        return sellerRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SellerResponse updateSeller(Long sellerId, UpdateSellerRequest request) {

        Seller seller = findSellerById(sellerId);

        if (request.getGstNumber() != null
                && sellerRepository.existsByGstNumberAndSellerIdNot(
                request.getGstNumber(), sellerId
        )) {
            throw new ResourceAlreadyExistsException(
                    "Seller already exists for this GST number"
            );
        }

        seller.setShopName(request.getShopName());
        seller.setShopDescription(request.getShopDescription());
        seller.setPhone(request.getPhone());
        seller.setGstNumber(request.getGstNumber());
        seller.setSellerType(request.getSellerType());

        return mapToResponse(sellerRepository.save(seller));
    }

    @Override
    public SellerResponse updateSellerStatus(
            Long sellerId,
            UpdateSellerStatusRequest request
    ) {

        Seller seller = findSellerById(sellerId);

        seller.setApprovalStatus(request.getApprovalStatus());

        return mapToResponse(sellerRepository.save(seller));
    }

    @Override
    public void deactivateSeller(Long sellerId) {

        Seller seller = findSellerById(sellerId);

        seller.setActive(false);

        sellerRepository.save(seller);
    }

    private Seller findSellerById(Long sellerId) {

        return sellerRepository.findBySellerIdAndActiveTrue(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found with ID: " + sellerId
                ));
    }

    private SellerResponse mapToResponse(Seller seller) {

        return SellerResponse.builder()
                .sellerId(seller.getSellerId())
                .userId(seller.getUserId())
                .shopName(seller.getShopName())
                .shopDescription(seller.getShopDescription())
                .phone(seller.getPhone())
                .gstNumber(seller.getGstNumber())
                .sellerType(seller.getSellerType())
                .approvalStatus(seller.getApprovalStatus())
                .createdAt(seller.getCreatedAt())
                .updatedAt(seller.getUpdatedAt())
                .build();
    }
}
