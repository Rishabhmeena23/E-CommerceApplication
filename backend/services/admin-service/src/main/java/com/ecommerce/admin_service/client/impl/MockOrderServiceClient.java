package com.ecommerce.admin_service.client.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.admin_service.client.OrderServiceClient;
import com.ecommerce.admin_service.dto.OrderDto;

@Service
public class MockOrderServiceClient implements OrderServiceClient {

    @Override
    public List<OrderDto> getAllOrders() {
        // Order Service is not part of this backend yet. Do not report fabricated data.
        return List.of();
    }
}
