package com.ecommerce.seller.controller;

import com.ecommerce.seller.dto.request.CreateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerRequest;
import com.ecommerce.seller.dto.request.UpdateSellerStatusRequest;
import com.ecommerce.seller.dto.response.SellerResponse;
import com.ecommerce.seller.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(
            @Valid @RequestBody CreateSellerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.createSeller(request));
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerResponse> getSellerById(
            @PathVariable Long sellerId
    ) {
        return ResponseEntity.ok(sellerService.getSellerById(sellerId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SellerResponse> getSellerByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(sellerService.getSellerByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<SellerResponse>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<SellerResponse> updateSeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody UpdateSellerRequest request
    ) {
        return ResponseEntity.ok(
                sellerService.updateSeller(sellerId, request)
        );
    }

    @PatchMapping("/{sellerId}/status")
    public ResponseEntity<SellerResponse> updateSellerStatus(
            @PathVariable Long sellerId,
            @Valid @RequestBody UpdateSellerStatusRequest request
    ) {
        return ResponseEntity.ok(
                sellerService.updateSellerStatus(sellerId, request)
        );
    }

    @DeleteMapping("/{sellerId}")
    public ResponseEntity<Void> deactivateSeller(
            @PathVariable Long sellerId
    ) {
        sellerService.deactivateSeller(sellerId);
        return ResponseEntity.noContent().build();
    }
}
