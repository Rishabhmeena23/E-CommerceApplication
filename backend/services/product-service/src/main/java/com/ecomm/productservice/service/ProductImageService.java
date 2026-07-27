package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.ProductImageRequest;
import com.ecomm.productservice.dto.response.ProductImageResponse;
import java.util.List;
public interface ProductImageService {

    List<ProductImageResponse> addImages(Long productId, List<ProductImageRequest> requests);
    ProductImageResponse updateImage(Long productId, Long imageId, ProductImageRequest request);
    void deleteImage(Long productId, Long imageId);
    List<ProductImageResponse> getImages(Long productId);

}