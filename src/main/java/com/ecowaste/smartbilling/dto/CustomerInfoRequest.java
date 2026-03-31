package com.ecowaste.smartbilling.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerInfoRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be 2 to 100 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Customer name should contain only letters and spaces")
    private String fullName;

    @NotBlank(message = "Customer phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String email;
}
