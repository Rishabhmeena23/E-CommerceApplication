package com.ecommerce.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_orders")
@Getter @Setter @NoArgsConstructor
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String customerEmail;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private OrderStatus status;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(nullable = false, length = 500) private String shippingAddress;
    private String paymentReference;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist void createTimestamps() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void updateTimestamp() { updatedAt = LocalDateTime.now(); }
    public void addItem(OrderItem item) { item.setOrder(this); items.add(item); }
}
