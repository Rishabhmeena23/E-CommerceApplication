package com.ecomm.productservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseModel{

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private int reservedQuantity =0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

}
