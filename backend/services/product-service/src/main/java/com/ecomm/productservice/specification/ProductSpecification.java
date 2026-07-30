package com.ecomm.productservice.specification;

import com.ecomm.productservice.model.Product;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public final class ProductSpecification {
    private ProductSpecification() {
    }
    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("sku")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern)
            );
        };
    }
    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subCategory").get("category").get("id"), categoryId);
        };
    }
    public static Specification<Product> hasSubCategoryId(Long subCategoryId) {
        return (root, query, cb) -> {
            if (subCategoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subCategory").get("id"), subCategoryId);
        };
    }
    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase());
        };
    }
    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }
    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
}
