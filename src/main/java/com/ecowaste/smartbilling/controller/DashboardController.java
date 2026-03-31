package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.ApiResponse;
import com.ecowaste.smartbilling.dto.DashboardResponse;
import com.ecowaste.smartbilling.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardResponse getDashboardStats(@RequestHeader("X-Store-Id") Long storeId) {
        return dashboardService.getDashboardStatistics(storeId);
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(true, "Dashboard module ready");
    }
}
