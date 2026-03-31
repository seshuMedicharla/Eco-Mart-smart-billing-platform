package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.OtpResponse;
import com.ecowaste.smartbilling.dto.OtpVerifyRequest;
import com.ecowaste.smartbilling.dto.OtpWidgetConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class Msg91OtpService {

    private final RestClient restClient;

    @Value("${app.msg91.enabled:false}")
    private boolean enabled;

    @Value("${app.msg91.widget-id:}")
    private String widgetId;

    @Value("${app.msg91.token-auth:}")
    private String tokenAuth;

    @Value("${app.msg91.authkey:}")
    private String authkey;

    @Value("${app.msg91.country-code:91}")
    private String countryCode;

    @Value("${app.msg91.otp-valid-minutes:5}")
    private long otpValidMinutes;

    public Msg91OtpService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://control.msg91.com")
                .build();
    }

    public OtpWidgetConfigResponse getWidgetConfig() {
        return new OtpWidgetConfigResponse(
                enabled,
                widgetId,
                tokenAuth,
                countryCode,
                otpValidMinutes
        );
    }

    public OtpResponse verifyAccessToken(OtpVerifyRequest request) {
        if (!enabled || widgetId.isBlank() || tokenAuth.isBlank() || authkey.isBlank()) {
            throw new IllegalStateException("MSG91 OTP is not configured correctly.");
        }

        Map<String, Object> payload = Map.of(
                "authkey", authkey,
                "access-token", request.getAccessToken().trim()
        );

        Map<String, Object> response;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> apiResponse = restClient.post()
                    .uri("/api/v5/widget/verifyAccessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
            response = apiResponse;
        } catch (Exception exception) {
            return new OtpResponse(false, "MSG91 verification failed. Please try again.", request.getPhoneNumber(), null, null, false, null);
        }

        if (response == null) {
            return new OtpResponse(false, "OTP verification failed. Empty response from MSG91.", request.getPhoneNumber(), null, null, false, null);
        }

        if (isFailureResponse(response)) {
            String message = String.valueOf(response.getOrDefault("message", "OTP verification failed."));
            return new OtpResponse(false, message, request.getPhoneNumber(), null, null, false, null);
        }

        return new OtpResponse(
                true,
                "OTP verified successfully",
                request.getPhoneNumber(),
                null,
                null,
                true,
                LocalDateTime.now().plusMinutes(otpValidMinutes)
        );
    }

    private boolean isFailureResponse(Map<String, Object> response) {
        String type = String.valueOf(response.getOrDefault("type", "")).toLowerCase();
        String status = String.valueOf(response.getOrDefault("status", "")).toLowerCase();
        String message = String.valueOf(response.getOrDefault("message", "")).toLowerCase();

        return type.contains("error")
                || type.contains("failed")
                || status.contains("error")
                || status.contains("failed")
                || message.contains("invalid")
                || message.contains("failed");
    }
}
