package com.ecomm.productservice.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int requested, int available) {
        super("Insufficient stock. Requested: " + requested + ", available: " + available);
    }
}
