package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.StoreLoginResponse;
import com.ecowaste.smartbilling.dto.StorePasswordResetRequest;
import com.ecowaste.smartbilling.dto.StoreRegistrationRequest;
import com.ecowaste.smartbilling.dto.StoreRegistrationResponse;
import com.ecowaste.smartbilling.service.StoreProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/store")
public class StoreProfileController {

    private final StoreProfileService storeProfileService;

    public StoreProfileController(StoreProfileService storeProfileService) {
        this.storeProfileService = storeProfileService;
    }

    @GetMapping("/status")
    public ResponseEntity<StoreRegistrationResponse> getStatus() {
        return ResponseEntity.ok(storeProfileService.getRegistrationStatus());
    }

    @PostMapping("/register")
    public ResponseEntity<StoreRegistrationResponse> registerStore(@Valid @RequestBody StoreRegistrationRequest request) {
        return ResponseEntity.ok(storeProfileService.registerStore(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<StoreLoginResponse> resetStorePassword(@Valid @RequestBody StorePasswordResetRequest request) {
        return ResponseEntity.ok(storeProfileService.resetStorePassword(request));
    }
}
