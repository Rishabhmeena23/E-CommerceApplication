package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.CategoryRequest;
import com.ecomm.productservice.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();

}

