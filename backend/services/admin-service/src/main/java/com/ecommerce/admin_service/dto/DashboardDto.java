package com.ecommerce.admin_service.dto;

public class DashboardDto {

    private Long totalUsers;

    private Long totalProducts;

    private Long totalOrders;

    private Long pendingDisputes;

    public DashboardDto() {
    }

    public DashboardDto(Long totalUsers,
                        Long totalProducts,
                        Long totalOrders,
                        Long pendingDisputes) {
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.pendingDisputes = pendingDisputes;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getPendingDisputes() {
        return pendingDisputes;
    }

    public void setPendingDisputes(Long pendingDisputes) {
        this.pendingDisputes = pendingDisputes;
    }
}