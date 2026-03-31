package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtpWidgetConfigResponse {
    private boolean enabled;
    private String widgetId;
    private String tokenAuth;
    private String countryCode;
    private long otpValidMinutes;
}
