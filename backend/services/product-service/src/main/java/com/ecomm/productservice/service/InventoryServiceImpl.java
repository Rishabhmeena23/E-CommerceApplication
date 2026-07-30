package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.InventoryRequest;
import com.ecomm.productservice.dto.request.StockAdjustmentRequest;
import com.ecomm.productservice.dto.response.InventoryResponse;
import com.ecomm.productservice.model.Inventory;
import com.ecomm.productservice.model.Product;
import com.ecomm.productservice.exception.BusinessRuleException;
import com.ecomm.productservice.exception.InsufficientStockException;
import com.ecomm.productservice.exception.InventoryNotFoundException;
import com.ecomm.productservice.mapper.InventoryMapper;
import com.ecomm.productservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductServiceImpl productService;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponse create(Long productId, InventoryRequest request) {
        Product product = productService.findProductOrThrow(productId);
        productService.requireProductOwner(product);
        if (inventoryRepository.existsByProductId(productId)) {
            throw new BusinessRuleException("Inventory already exists for product id: " + productId);
        }
        Inventory inventory = inventoryMapper.toEntity(request);
        if (inventory.getReservedQuantity() > inventory.getQuantity()) {
            throw new BusinessRuleException("Reserved quantity cannot exceed total quantity");
        }
        inventory.setProduct(product);
        product.setInventory(inventory);
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse updateStock(Long productId, InventoryRequest request) {
        Inventory inventory = findInventoryOrThrow(productId);
        productService.requireProductOwner(inventory.getProduct());
        inventoryMapper.updateEntity(request, inventory);
        validateQuantities(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse increaseStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = findInventoryOrThrow(productId);
        productService.requireProductOwner(inventory.getProduct());
        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse decreaseStock(Long productId, StockAdjustmentRequest request) {
        Inventory inventory = findInventoryOrThrow(productId);
        productService.requireProductOwner(inventory.getProduct());
        int available = inventory.getQuantity() - inventory.getReservedQuantity();
        if (request.getQuantity() > available) {
            throw new InsufficientStockException(request.getQuantity(), available);
        }
        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse getInventory(Long productId) {
        return inventoryMapper.toResponse(findInventoryOrThrow(productId));
    }

    private Inventory findInventoryOrThrow(Long productId) {
        productService.findProductOrThrow(productId);
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    private void validateQuantities(Inventory inventory) {
        if (inventory.getReservedQuantity() > inventory.getQuantity()) {
            throw new BusinessRuleException("Reserved quantity cannot exceed total quantity");
        }
    }
}
