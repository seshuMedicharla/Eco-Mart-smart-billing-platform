package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BillHistoryResponse {
    private Long billId;
    private String invoiceNumber;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal nextVisitDiscountPercent;
    private LocalDateTime createdDate;
}
