package com.ecommerce.admin_service.client;

import java.util.List;

import com.ecommerce.admin_service.dto.OrderDto;

public interface OrderServiceClient {

    List<OrderDto> getAllOrders();

}
