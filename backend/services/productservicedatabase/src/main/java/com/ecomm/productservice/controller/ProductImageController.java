package com.ecomm.productservice.controller;

import com.ecomm.productservice.dto.request.ProductImageRequest;
import com.ecomm.productservice.dto.response.ProductImageResponse;
import com.ecomm.productservice.service.ProductImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<List<ProductImageResponse>> addImages(
            @PathVariable Long productId,
            @Valid @RequestBody List<@Valid ProductImageRequest> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productImageService.addImages(productId, requests));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> updateImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        return ResponseEntity.ok(productImageService.updateImage(productId, imageId, request));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getImages(productId));
    }
}