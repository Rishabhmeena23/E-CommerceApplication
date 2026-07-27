package com.ecomm.productservice.exception;

public class SubCategoryNotFoundException extends RuntimeException {
    public SubCategoryNotFoundException(Long id) {
        super("SubCategory not found with id: " + id);
    }
}