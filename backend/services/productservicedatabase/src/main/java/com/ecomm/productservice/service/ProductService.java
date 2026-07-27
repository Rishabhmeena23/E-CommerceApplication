package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.ProductRequest;
import com.ecomm.productservice.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    ProductResponse getById(Long id);

    List<ProductResponse> getAll();

    Page<ProductResponse> search(
            String keyword,
            Long categoryId,
            Long subCategoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
}
