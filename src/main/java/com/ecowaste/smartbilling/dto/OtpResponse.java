package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OtpResponse {
    private boolean success;
    private String message;
    private String phoneNumber;
    private String otp;
    private String targetEmail;
    private boolean verified;
    private LocalDateTime expiresAt;
}
