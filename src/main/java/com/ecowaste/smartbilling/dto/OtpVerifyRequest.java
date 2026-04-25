package com.ecowaste.smartbilling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private String accessToken;

    @Pattern(regexp = "^\\d{4}$", message = "OTP must be exactly 4 digits")
    private String otpCode;
}
