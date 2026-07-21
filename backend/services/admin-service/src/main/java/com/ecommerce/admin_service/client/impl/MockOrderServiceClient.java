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

        return List.of(

                new OrderDto(
                        101L,
                        1L,
                        new BigDecimal("56000"),
                        "DELIVERED"),

                new OrderDto(
                        102L,
                        2L,
                        new BigDecimal("1200"),
                        "PENDING")

        );

    }

}
