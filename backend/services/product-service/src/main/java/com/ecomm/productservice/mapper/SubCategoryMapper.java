package com.ecomm.productservice.mapper;

import com.ecomm.productservice.dto.request.SubCategoryRequest;
import com.ecomm.productservice.dto.response.SubCategoryResponse;
import com.ecomm.productservice.model.SubCategory;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SubCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "allProducts", ignore = true)
    SubCategory toEntity(SubCategoryRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    SubCategoryResponse toResponse(SubCategory subCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "allProducts", ignore = true)
    void updateEntity(SubCategoryRequest request, @MappingTarget SubCategory subCategory);
}

