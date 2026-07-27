package com.ecomm.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {
    @NotBlank
    @Size(max = 500)
    private String url;

    @Size(max = 200)
    private String altText;

    private boolean primaryImage;

    @PositiveOrZero
    private int displayOrder;
}
