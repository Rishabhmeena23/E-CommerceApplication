package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.InventoryRequest;
import com.ecomm.productservice.dto.request.StockAdjustmentRequest;
import com.ecomm.productservice.dto.response.InventoryResponse;
public interface InventoryService {

    InventoryResponse create(Long productId, InventoryRequest request);
    InventoryResponse updateStock(Long productId, InventoryRequest request);
    InventoryResponse increaseStock(Long productId, StockAdjustmentRequest request);
    InventoryResponse decreaseStock(Long productId, StockAdjustmentRequest request);
    InventoryResponse getInventory(Long productId);
}
