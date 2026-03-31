package com.ecowaste.smartbilling.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageUpdateRequest {

    @NotBlank(message = "Product image is required")
    private String image;
}
