package com.ecowaste.smartbilling.dto;

import com.ecowaste.smartbilling.model.WasteCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BillItemSaveResponse {
    private Long productId;
    private String productName;
    private WasteCategory wasteCategory;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private Integer remainingStock;
}
