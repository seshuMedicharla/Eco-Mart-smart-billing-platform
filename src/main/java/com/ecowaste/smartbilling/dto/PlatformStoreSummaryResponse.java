package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PlatformStoreSummaryResponse {
    private Long storeId;
    private String storeName;
    private String ownerName;
    private String loginUsername;
    private Boolean active;
    private Long totalProducts;
    private Long totalCustomers;
    private Long totalBills;
    private BigDecimal totalSales;
}
