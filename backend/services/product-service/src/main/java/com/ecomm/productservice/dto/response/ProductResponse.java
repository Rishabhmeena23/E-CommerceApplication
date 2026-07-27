package com.ecomm.productservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String brand;
    private BigDecimal price;
    private Long subCategoryId;
    private String subCategoryName;
    private Long categoryId;
    private String categoryName;
    private InventoryResponse inventory;
    private List<ProductImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

