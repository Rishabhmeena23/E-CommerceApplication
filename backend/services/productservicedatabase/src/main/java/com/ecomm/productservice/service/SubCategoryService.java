package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.SubCategoryRequest;
import com.ecomm.productservice.dto.response.SubCategoryResponse;
import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest request);
    SubCategoryResponse update(Long id, SubCategoryRequest request);
    void delete(Long id);
    SubCategoryResponse getById(Long id);
    List<SubCategoryResponse> getAll(Long categoryId);
}
