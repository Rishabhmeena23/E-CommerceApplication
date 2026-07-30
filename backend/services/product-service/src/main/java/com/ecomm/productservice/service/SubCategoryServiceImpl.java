package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.SubCategoryRequest;
import com.ecomm.productservice.dto.response.SubCategoryResponse;
import com.ecomm.productservice.model.Category;
import com.ecomm.productservice.model.SubCategory;
import com.ecomm.productservice.exception.BusinessRuleException;
import com.ecomm.productservice.exception.CategoryNotFoundException;
import com.ecomm.productservice.exception.SubCategoryNotFoundException;
import com.ecomm.productservice.mapper.SubCategoryMapper;
import com.ecomm.productservice.repository.CategoryRepository;
import com.ecomm.productservice.repository.SubCategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @Override
    @Transactional
    public SubCategoryResponse create(SubCategoryRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        if (subCategoryRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), request.getCategoryId())) {
            throw new BusinessRuleException("SubCategory already exists with name: " + request.getName());
        }
        SubCategory subCategory = subCategoryMapper.toEntity(request);
        subCategory.setCategory(category);
        return subCategoryMapper.toResponse(subCategoryRepository.save(subCategory));
    }

    @Override
    @Transactional
    public SubCategoryResponse update(Long id, SubCategoryRequest request) {
        SubCategory subCategory = findSubCategoryOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        if (subCategoryRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), request.getCategoryId())
                && !subCategory.getName().equalsIgnoreCase(request.getName())) {
            throw new BusinessRuleException("SubCategory already exists with name: " + request.getName());
        }
        subCategoryMapper.updateEntity(request, subCategory);
        subCategory.setCategory(category);
        return subCategoryMapper.toResponse(subCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SubCategory subCategory = findSubCategoryOrThrow(id);
        if (subCategory.getAllProducts().stream()
                .anyMatch(product -> !product.isDeleted())) {
            throw new BusinessRuleException("Cannot delete sub-category with existing products");
        }
        subCategory.setDeleted(true);
        subCategoryRepository.save(subCategory);
    }

    @Override
    public SubCategoryResponse getById(Long id) {
        return subCategoryMapper.toResponse(findSubCategoryOrThrow(id));
    }

    @Override
    public List<SubCategoryResponse> getAll(Long categoryId) {
        List<SubCategory> subCategories = categoryId == null
                ? subCategoryRepository.findAll()
                : subCategoryRepository.findByCategoryId(categoryId);
        return subCategories.stream()
                .filter(subCategory -> !subCategory.isDeleted())
                .map(subCategoryMapper::toResponse)
                .toList();
    }
    SubCategory findSubCategoryOrThrow(Long id) {
        return subCategoryRepository.findById(id)
                .filter(subCategory -> !subCategory.isDeleted())
                .orElseThrow(() -> new SubCategoryNotFoundException(id));
    }
}
