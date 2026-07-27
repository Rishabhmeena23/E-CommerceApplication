package com.ecomm.productservice.mapper;

import com.ecomm.productservice.dto.request.InventoryRequest;
import com.ecomm.productservice.dto.response.InventoryResponse;
import com.ecomm.productservice.model.Inventory;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reservedQuantity", defaultExpression = "java(0)")
    Inventory toEntity(InventoryRequest request);
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "availableQuantity", source = ".", qualifiedByName = "availableQuantity")
    InventoryResponse toResponse(Inventory inventory);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(InventoryRequest request, @MappingTarget Inventory inventory);
    @Named("availableQuantity")
    default int availableQuantity(Inventory inventory) {
        return inventory.getQuantity() - inventory.getReservedQuantity();
    }
}