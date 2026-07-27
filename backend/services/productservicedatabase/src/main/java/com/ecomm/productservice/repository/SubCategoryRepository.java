package com.ecomm.productservice.repository;

import com.ecomm.productservice.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategoryId(Long categoryId);
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);
}
