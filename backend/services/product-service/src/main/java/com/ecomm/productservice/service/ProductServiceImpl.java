package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.ProductRequest;
import com.ecomm.productservice.dto.response.ProductResponse;
import com.ecomm.productservice.model.Product;
import com.ecomm.productservice.model.SubCategory;
import com.ecomm.productservice.exception.DuplicateSkuException;
import com.ecomm.productservice.exception.ProductNotFoundException;
import com.ecomm.productservice.exception.SubCategoryNotFoundException;
import com.ecomm.productservice.mapper.ProductMapper;
import com.ecomm.productservice.repository.ProductRepository;
import com.ecomm.productservice.repository.SubCategoryRepository;
import com.ecomm.productservice.specification.ProductSpecification;
import com.ecomm.productservice.security.AuthenticatedUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new SubCategoryNotFoundException(request.getSubCategoryId()));
        Product product = productMapper.toEntity(request);
        product.setSellerUserId(currentUserId());
        product.setSubCategory(subCategory);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        requireProductOwner(product);
        productRepository.findBySkuIgnoreCase(request.getSku())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateSkuException(request.getSku());
                });
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new SubCategoryNotFoundException(request.getSubCategoryId()));
        productMapper.updateEntity(request, product);
        product.setSubCategory(subCategory);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        requireProductOwner(product);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .filter(product -> !product.isDeleted())
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public Page<ProductResponse> search(
            String keyword,
            Long categoryId,
            Long subCategoryId,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        Specification<Product> specification = Specification
                .where(ProductSpecification.isNotDeleted())
                .and(ProductSpecification.hasKeyword(keyword))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasSubCategoryId(subCategoryId))
                .and(ProductSpecification.hasBrand(brand))
                .and(ProductSpecification.hasMinPrice(minPrice))
                .and(ProductSpecification.hasMaxPrice(maxPrice));
        return productRepository.findAll(specification, pageable)
                .map(productMapper::toResponse);
    }
    Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    void requireProductOwner(Product product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (!admin && !product.getSellerUserId().equals(currentUserId())) {
            throw new AccessDeniedException("You can only modify your own products");
        }
    }

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user.userId();
        }
        throw new AccessDeniedException("Authenticated user identity is unavailable");
    }
}
