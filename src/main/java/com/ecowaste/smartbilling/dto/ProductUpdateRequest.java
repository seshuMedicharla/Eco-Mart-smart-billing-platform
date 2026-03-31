package com.ecowaste.smartbilling.dto;

import com.ecowaste.smartbilling.model.WasteCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 120, message = "Product name must be 2 to 120 characters")
    private String name;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
    private BigDecimal price;

    private String image;

    @NotNull(message = "Waste category is required")
    private WasteCategory wasteCategory;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
