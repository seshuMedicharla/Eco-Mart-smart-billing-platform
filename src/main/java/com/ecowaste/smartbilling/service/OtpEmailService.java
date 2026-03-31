package com.ecowaste.smartbilling.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OtpEmailService {

    private static final DateTimeFormatter OTP_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.otp.email.enabled:false}")
    private boolean emailOtpEnabled;

    @Value("${app.otp.email.from:}")
    private String fromAddress;

    @Value("${spring.application.name:EcoWaste Smart Billing System}")
    private String applicationName;

    public OtpEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean isEmailOtpEnabled() {
        return emailOtpEnabled;
    }

    public void sendOtp(String customerEmail, String otpCode, LocalDateTime expiresAt) {
        if (!emailOtpEnabled) {
            throw new IllegalStateException("Email OTP delivery is disabled.");
        }

        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email is required for OTP delivery.");
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("OTP sender email is not configured.");
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Mail sender is not available.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(customerEmail.trim());
        message.setSubject(applicationName + " verification code");
        message.setText(buildOtpMailBody(otpCode, expiresAt));

        mailSender.send(message);
    }

    private String buildOtpMailBody(String otpCode, LocalDateTime expiresAt) {
        return """
                Your MegaMart verification code is: %s

                This code will expire at %s.

                If you did not request this code, please ignore this email.

                Regards,
                MegaMart Smart Billing
                """.formatted(otpCode, expiresAt.format(OTP_TIME_FORMAT));
    }
}
