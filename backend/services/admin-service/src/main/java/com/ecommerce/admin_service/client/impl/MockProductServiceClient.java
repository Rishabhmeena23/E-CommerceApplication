package com.ecommerce.admin_service.client.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.admin_service.client.ProductServiceClient;
import com.ecommerce.admin_service.dto.ProductDto;

@Service
public class MockProductServiceClient implements ProductServiceClient {

    @Override
    public List<ProductDto> getAllProducts() {

        return List.of(

                new ProductDto(
                        1L,
                        "Laptop",
                        "Gaming Laptop",
                        "Electronics",
                        new BigDecimal("75000"),
                        "AVAILABLE"
                ),

                new ProductDto(
                        2L,
                        "Smartphone",
                        "Android Smartphone",
                        "Electronics",
                        new BigDecimal("30000"),
                        "AVAILABLE"
                )
        );
    }
}