package com.ecowaste.smartbilling.dto;

import com.ecowaste.smartbilling.model.WasteCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private Integer stockQuantity;
    private WasteCategory wasteCategory;
}
