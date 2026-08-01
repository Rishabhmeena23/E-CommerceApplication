package com.ecommerce.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "payments") @Getter @Setter @NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String paymentReference;
    @Column(nullable = false) private Long orderId;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 30) private String paymentMethod;
    @Column(length = 4) private String lastFour;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentStatus status;
    private String failureReason;
    @Column(nullable = false) private LocalDateTime createdAt;
    @PrePersist void timestamp() { createdAt = LocalDateTime.now(); }
}
