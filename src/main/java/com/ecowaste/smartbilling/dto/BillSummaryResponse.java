package com.ecowaste.smartbilling.dto;

import com.ecowaste.smartbilling.model.WasteCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BillSummaryResponse {
    private List<BillSummaryItemResponse> selectedItems;
    private BigDecimal totalAmount;
    private BigDecimal nextVisitDiscountPercent;
    private WasteCategory discountBasedOnCategory;
    private Map<String, CategoryBreakdownResponse> categoryBreakdown;
    private String message;
}
