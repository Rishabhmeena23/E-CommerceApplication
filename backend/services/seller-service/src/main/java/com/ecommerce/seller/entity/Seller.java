package com.ecommerce.seller.entity;

import com.ecommerce.seller.entity.enums.ApprovalStatus;
import com.ecommerce.seller.entity.enums.SellerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String shopName;

    @Column(columnDefinition = "TEXT")
    private String shopDescription;

    @Column(nullable = false, length = 20)
    private String phone;

    // GST Number
    @Column(name = "gst_number", unique = true, length = 15)
    private String gstNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SellerType sellerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus approvalStatus;

    // Soft Delete Flag
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}