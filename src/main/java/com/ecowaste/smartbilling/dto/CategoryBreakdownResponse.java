package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CategoryBreakdownResponse {
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private BigDecimal nextVisitDiscountPercent;
}
