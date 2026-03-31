package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.AdminLoginRequest;
import com.ecowaste.smartbilling.dto.AdminLoginResponse;
import com.ecowaste.smartbilling.dto.ApiResponse;
import com.ecowaste.smartbilling.dto.ErrorResponse;
import com.ecowaste.smartbilling.dto.StoreLoginResponse;
import com.ecowaste.smartbilling.service.AdminService;
import com.ecowaste.smartbilling.service.StoreProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminService adminService;
    private final StoreProfileService storeProfileService;

    public AuthController(AdminService adminService, StoreProfileService storeProfileService) {
        this.adminService = adminService;
        this.storeProfileService = storeProfileService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.login(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/store-login")
    public ResponseEntity<StoreLoginResponse> storeLogin(@Valid @RequestBody AdminLoginRequest request) {
        StoreLoginResponse response = storeProfileService.loginStore(request);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(true, "Auth module ready");
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
