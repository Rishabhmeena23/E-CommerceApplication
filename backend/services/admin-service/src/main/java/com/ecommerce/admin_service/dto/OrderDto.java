package com.ecommerce.admin_service.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonAlias;

public class OrderDto {

    private Long id;

    private Long userId;

    private BigDecimal totalAmount;

    @JsonAlias("status")
    private String orderStatus;

    public OrderDto() {
    }

    public OrderDto(Long id, Long userId,
                    BigDecimal totalAmount,
                    String orderStatus) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
