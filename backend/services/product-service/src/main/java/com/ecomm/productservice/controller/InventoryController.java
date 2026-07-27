package com.ecomm.productservice.controller;

import com.ecomm.productservice.dto.request.InventoryRequest;
import com.ecomm.productservice.dto.request.StockAdjustmentRequest;
import com.ecomm.productservice.dto.response.InventoryResponse;
import com.ecomm.productservice.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/{productId}/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(productId, request));
    }

    @PutMapping
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request
    ) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, request));
    }

    @PatchMapping("/increase")
    public ResponseEntity<InventoryResponse> increaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        return ResponseEntity.ok(inventoryService.increaseStock(productId, request));
    }

    @PatchMapping("/decrease")
    public ResponseEntity<InventoryResponse> decreaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        return ResponseEntity.ok(inventoryService.decreaseStock(productId, request));
    }

    @GetMapping
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }
}