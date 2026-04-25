package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.ApiResponse;
import com.ecowaste.smartbilling.dto.ErrorResponse;
import com.ecowaste.smartbilling.dto.OtpGenerateRequest;
import com.ecowaste.smartbilling.dto.OtpResponse;
import com.ecowaste.smartbilling.dto.OtpVerifyRequest;
import com.ecowaste.smartbilling.dto.OtpWidgetConfigResponse;
import com.ecowaste.smartbilling.service.CustomerService;
import com.ecowaste.smartbilling.service.Msg91OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final Msg91OtpService msg91OtpService;

    public CustomerController(CustomerService customerService, Msg91OtpService msg91OtpService) {
        this.customerService = customerService;
        this.msg91OtpService = msg91OtpService;
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody OtpGenerateRequest request) {
        return ResponseEntity.ok(customerService.generateOtp(request));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<OtpResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        OtpResponse response = msg91OtpService.isConfigured() && request.getAccessToken() != null && !request.getAccessToken().isBlank()
                ? msg91OtpService.verifyAccessToken(request)
                : customerService.verifyOtp(request.getPhoneNumber(), request.getOtpCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/otp/config")
    public ResponseEntity<OtpWidgetConfigResponse> getOtpWidgetConfig() {
        return ResponseEntity.ok(msg91OtpService.getWidgetConfig());
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(true, "Customer module ready");
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
