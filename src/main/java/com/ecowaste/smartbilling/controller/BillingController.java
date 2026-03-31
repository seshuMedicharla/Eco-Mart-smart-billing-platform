package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.ApiResponse;
import com.ecowaste.smartbilling.dto.BillRequest;
import com.ecowaste.smartbilling.dto.BillSaveRequest;
import com.ecowaste.smartbilling.dto.BillSaveResponse;
import com.ecowaste.smartbilling.dto.BillHistoryResponse;
import com.ecowaste.smartbilling.dto.BillSummaryResponse;
import com.ecowaste.smartbilling.dto.ErrorResponse;
import com.ecowaste.smartbilling.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<BillSummaryResponse> calculateBill(@RequestHeader("X-Store-Id") Long storeId,
                                                             @Valid @RequestBody BillRequest request) {
        return ResponseEntity.ok(billingService.generateBillSummary(storeId, request));
    }

    @PostMapping("/save")
    public ResponseEntity<BillSaveResponse> saveBill(@RequestHeader("X-Store-Id") Long storeId,
                                                     @Valid @RequestBody BillSaveRequest request) {
        return ResponseEntity.ok(billingService.saveBill(storeId, request));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<BillSaveResponse> getBillByInvoiceNumber(@RequestHeader("X-Store-Id") Long storeId,
                                                                   @PathVariable String invoiceNumber) {
        return ResponseEntity.ok(billingService.getBillByInvoiceNumber(storeId, invoiceNumber));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<BillHistoryResponse>> getRecentBills(@RequestHeader("X-Store-Id") Long storeId) {
        return ResponseEntity.ok(billingService.getRecentBills(storeId));
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(true, "Billing module ready");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(false, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> ((FieldError) error).getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new ErrorResponse(false, message));
    }
}
