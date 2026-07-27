package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.CategoryRequest;
import com.ecomm.productservice.dto.response.CategoryResponse;
import com.ecomm.productservice.model.Category;
import com.ecomm.productservice.exception.BusinessRuleException;
import com.ecomm.productservice.exception.CategoryNotFoundException;
import com.ecomm.productservice.mapper.CategoryMapper;
import com.ecomm.productservice.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessRuleException("Category already exists with name: " + request.getName());
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Category already exists with name: " + request.getName());
                });
        categoryMapper.updateEntity(request, category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findCategoryOrThrow(id);
        if (!category.getSubCategories().isEmpty()) {
            throw new BusinessRuleException("Cannot delete category with existing sub-categories");
        }
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(findCategoryOrThrow(id));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
