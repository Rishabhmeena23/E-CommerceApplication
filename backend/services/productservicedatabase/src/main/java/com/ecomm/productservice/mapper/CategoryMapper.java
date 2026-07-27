package com.ecomm.productservice.mapper;

import com.ecomm.productservice.dto.request.CategoryRequest;
import com.ecomm.productservice.dto.response.CategoryResponse;
import com.ecomm.productservice.model.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
