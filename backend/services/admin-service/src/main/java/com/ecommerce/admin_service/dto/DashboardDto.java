package com.ecommerce.admin_service.dto;


public class DashboardDto {

    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private long pendingDisputes;

    public DashboardDto() {
    }

    public DashboardDto(long totalUsers,
                        long totalProducts,
                        long totalOrders,
                        long pendingDisputes) {
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.pendingDisputes = pendingDisputes;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getPendingDisputes() {
        return pendingDisputes;
    }

    public void setPendingDisputes(long pendingDisputes) {
        this.pendingDisputes = pendingDisputes;
    }
}
