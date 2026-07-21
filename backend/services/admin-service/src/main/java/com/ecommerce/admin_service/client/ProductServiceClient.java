package com.ecommerce.admin_service.client;


import java.util.List;

import com.ecommerce.admin_service.dto.ProductDto;

public interface ProductServiceClient {

    List<ProductDto> getAllProducts();

}
