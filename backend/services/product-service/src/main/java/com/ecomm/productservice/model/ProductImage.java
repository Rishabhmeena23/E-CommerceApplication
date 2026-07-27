package com.ecomm.productservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends BaseModel{

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200)
    private String altText;

    @Column(nullable = false)
    @Builder.Default
    private boolean primaryImage = false;

    @Column(nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}



