package com.ecommerce.seller.dto.request;

import com.ecommerce.seller.entity.enums.SellerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSellerRequest {

    @NotBlank(message = "Shop name is required")
    @Size(max = 150, message = "Shop name must not exceed 150 characters")
    private String shopName;

    private String shopDescription;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    @Pattern(
            regexp = "^$|^[0-9A-Z]{15}$",
            message = "GST number must be 15 uppercase letters and numbers"
    )
    private String gstNumber;

    @NotNull(message = "Seller type is required")
    private SellerType sellerType;
}
