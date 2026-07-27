package com.ecomm.productservice.mapper;

import com.ecomm.productservice.dto.request.ProductImageRequest;
import com.ecomm.productservice.dto.request.ProductRequest;
import com.ecomm.productservice.dto.response.ProductImageResponse;
import com.ecomm.productservice.dto.response.ProductResponse;
import com.ecomm.productservice.model.Product;
import com.ecomm.productservice.model.ProductImage;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring", uses = {InventoryMapper.class}, builder = @Builder(disableBuilder = true))
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "inventory", ignore = true)
//    @Mapping(target = "sku", ignore = true)
//    @Mapping(target = "brand", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "subCategoryId", source = "subCategory.id")
    @Mapping(target = "subCategoryName", source = "subCategory.name")
    @Mapping(target = "categoryId", source = "subCategory.category.id")
    @Mapping(target = "categoryName", source = "subCategory.category.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "subCategory", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage toImageEntity(ProductImageRequest request);

    @Mapping(target = "productId", source = "product.id")
    ProductImageResponse toImageResponse(ProductImage image);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateImageEntity(ProductImageRequest request, @MappingTarget ProductImage image);
}
