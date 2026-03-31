package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalSales;
    private Long recyclableItemsSold;
    private Long reusableItemsSold;
    private Long ecoDisposalItemsSold;
    private Long totalBillsGenerated;
}
