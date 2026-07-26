package com.ecommerce.wishlist_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id")
    @JsonBackReference
    private Wishlist wishlist;

    @Column(nullable = false)
    private Long productId;

}