package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlatformOverviewResponse {
    private boolean success;
    private String message;
    private Long totalStores;
    private Long activeStores;
    private Long totalPlatformBills;
    private BigDecimal totalPlatformSales;
    private List<PlatformStoreSummaryResponse> stores;
}
