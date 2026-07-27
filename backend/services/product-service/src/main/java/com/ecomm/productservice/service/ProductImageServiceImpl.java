package com.ecomm.productservice.service;

import com.ecomm.productservice.dto.request.ProductImageRequest;
import com.ecomm.productservice.dto.response.ProductImageResponse;
import com.ecomm.productservice.model.Product;
import com.ecomm.productservice.model.ProductImage;
import com.ecomm.productservice.exception.ProductImageNotFoundException;
import com.ecomm.productservice.mapper.ProductMapper;
import com.ecomm.productservice.repository.ProductImageRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductServiceImpl productService;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public List<ProductImageResponse> addImages(Long productId, List<ProductImageRequest> requests) {
        Product product = productService.findProductOrThrow(productId);
        List<ProductImage> images = requests.stream()
                .map(request -> {
                    ProductImage image = productMapper.toImageEntity(request);
                    image.setProduct(product);
                    if (image.isPrimaryImage()) {
                        clearPrimaryFlag(product);
                    }
                    return image;
                })
                .toList();
        return productImageRepository.saveAll(images).stream()
                .map(productMapper::toImageResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductImageResponse updateImage(Long productId, Long imageId, ProductImageRequest request) {
        ProductImage image = findImageOrThrow(productId, imageId);
        if (request.isPrimaryImage()) {
            clearPrimaryFlag(image.getProduct());
        }
        productMapper.updateImageEntity(request, image);
        return productMapper.toImageResponse(image);
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = findImageOrThrow(productId, imageId);
        image.setDeleted(false);
        productImageRepository.save(image);
    }

    @Override
    public List<ProductImageResponse> getImages(Long productId) {
        productService.findProductOrThrow(productId);
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(productMapper::toImageResponse)
                .toList();
    }

    private ProductImage findImageOrThrow(Long productId, Long imageId) {
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ProductImageNotFoundException(imageId));
    }

    private void clearPrimaryFlag(Product product) {
        productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId())
                .forEach(image -> image.setPrimaryImage(false));
    }
}
