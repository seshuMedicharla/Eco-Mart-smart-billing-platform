package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BillSaveResponse {
    private boolean success;
    private String message;
    private Long billId;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private BigDecimal subtotal;
    private BigDecimal appliedDiscountPercent;
    private BigDecimal appliedDiscountAmount;
    private BigDecimal totalAmount;
    private BigDecimal nextVisitDiscountPercent;
    private LocalDateTime createdDate;
    private List<BillItemSaveResponse> items;
    private SmsSummaryResponse smsSummary;
}
