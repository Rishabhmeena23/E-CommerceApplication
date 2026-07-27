package com.ecomm.productservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseModel{

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubCategory> subCategories = new ArrayList<>();

//    @OneToMany(mappedBy = "category")
//    private List<Product> allProducts;
//
//    @OneToMany
//    private List<Product> featuredProducts;

}

