package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.AdminLoginRequest;
import com.ecowaste.smartbilling.dto.PlatformOverviewResponse;
import com.ecowaste.smartbilling.service.PlatformAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/platform")
public class PlatformAdminController {

    private final PlatformAdminService platformAdminService;

    public PlatformAdminController(PlatformAdminService platformAdminService) {
        this.platformAdminService = platformAdminService;
    }

    @PostMapping("/overview")
    public ResponseEntity<PlatformOverviewResponse> getOverview(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(platformAdminService.getOverview(request));
    }
}
