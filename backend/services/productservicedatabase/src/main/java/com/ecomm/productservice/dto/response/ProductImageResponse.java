package com.ecomm.productservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private Long id;
    private Long productId;
    private String url;
    private String altText;
    private boolean primaryImage;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
