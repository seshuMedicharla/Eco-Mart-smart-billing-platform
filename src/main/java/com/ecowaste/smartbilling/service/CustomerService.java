package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.OtpGenerateRequest;
import com.ecowaste.smartbilling.dto.OtpResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {

    private static final long OTP_VALID_MINUTES = 5L;

    private final Map<String, OtpSession> otpStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final OtpEmailService otpEmailService;

    public CustomerService(OtpEmailService otpEmailService) {
        this.otpEmailService = otpEmailService;
    }

    public OtpResponse generateOtp(OtpGenerateRequest request) {
        String normalizedPhone = normalizePhoneNumber(request.getPhoneNumber());
        String normalizedEmail = normalizeEmailAddress(request.getEmail());
        String otp = String.format("%04d", random.nextInt(10000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES);

        if (!otpEmailService.isEmailOtpEnabled()) {
            throw new IllegalStateException("Email OTP is not configured. Enable mail settings before sending verification codes.");
        }

        otpStore.put(normalizedPhone, new OtpSession(otp, expiresAt, normalizedEmail));

        try {
            otpEmailService.sendOtp(normalizedEmail, otp, expiresAt);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not send OTP email. Check mail settings and try again.");
        }

        return new OtpResponse(
                true,
                "Verification code sent to " + maskEmailAddress(normalizedEmail) + ". It will expire in 5 minutes.",
                normalizedPhone,
                null,
                maskEmailAddress(normalizedEmail),
                false,
                expiresAt
        );
    }

    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber == null ? "" : phoneNumber.replaceAll("\\s+", "");
    }

    private String normalizeEmailAddress(String emailAddress) {
        return emailAddress == null ? "" : emailAddress.trim().toLowerCase();
    }

    private String maskEmailAddress(String emailAddress) {
        if (emailAddress == null || emailAddress.isBlank() || !emailAddress.contains("@")) {
            return null;
        }

        String[] parts = emailAddress.split("@", 2);
        String localPart = parts[0];
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + parts[1];
        }

        return localPart.substring(0, 2) + "*".repeat(Math.max(1, localPart.length() - 2)) + "@" + parts[1];
    }

    private record OtpSession(String code, LocalDateTime expiresAt, String email) {
    }
}
