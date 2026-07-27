package com.ecomm.productservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long productId;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
