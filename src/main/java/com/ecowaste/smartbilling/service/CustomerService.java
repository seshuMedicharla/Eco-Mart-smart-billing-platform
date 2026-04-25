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

        otpStore.put(normalizedPhone, new OtpSession(otp, expiresAt, normalizedEmail));

        if (otpEmailService.isEmailOtpEnabled()) {
            try {
                otpEmailService.sendOtp(normalizedEmail, otp, expiresAt);
                return new OtpResponse(
                        true,
                        "Verification code sent to " + maskEmailAddress(normalizedEmail) + ". It will expire in 5 minutes.",
                        normalizedPhone,
                        null,
                        maskEmailAddress(normalizedEmail),
                        false,
                        expiresAt
                );
            } catch (Exception exception) {
                return new OtpResponse(
                        true,
                        "Email OTP delivery is unavailable locally. Use the generated code shown on screen. It will expire in 5 minutes.",
                        normalizedPhone,
                        otp,
                        maskEmailAddress(normalizedEmail),
                        false,
                        expiresAt
                );
            }
        }

        return new OtpResponse(
                true,
                "Local OTP generated successfully. It will expire in 5 minutes.",
                normalizedPhone,
                otp,
                maskEmailAddress(normalizedEmail),
                false,
                expiresAt
        );
    }

    public OtpResponse verifyOtp(String phoneNumber, String otpCode) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);
        String normalizedOtp = otpCode == null ? "" : otpCode.trim();
        OtpSession session = otpStore.get(normalizedPhone);

        if (session == null) {
            return new OtpResponse(false, "Generate OTP before verification.", normalizedPhone, null, null, false, null);
        }

        if (session.expiresAt().isBefore(LocalDateTime.now())) {
            otpStore.remove(normalizedPhone);
            return new OtpResponse(false, "Verification code expired. Please resend OTP.", normalizedPhone, null, null, false, null);
        }

        if (!session.code().equals(normalizedOtp)) {
            return new OtpResponse(false, "OTP verification failed. Please enter the correct OTP and try again.", normalizedPhone, null, null, false, session.expiresAt());
        }

        otpStore.remove(normalizedPhone);
        return new OtpResponse(true, "OTP verified successfully.", normalizedPhone, null, maskEmailAddress(session.email()), true, session.expiresAt());
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
