package com.ecowaste.smartbilling.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRegistrationRequest {

    @NotBlank(message = "Platform admin username is required")
    private String platformUsername;

    @NotBlank(message = "Platform admin password is required")
    private String platformPassword;

    @NotBlank(message = "Store name is required")
    @Size(min = 2, max = 120, message = "Store name must be 2 to 120 characters")
    private String storeName;

    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 100, message = "Owner name must be 2 to 100 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Owner name should contain only letters and spaces")
    private String ownerName;

    @NotBlank(message = "Store phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Store email is required")
    @Email(message = "Store email must be valid")
    private String email;

    @NotBlank(message = "Store address is required")
    @Size(min = 5, max = 255, message = "Store address must be 5 to 255 characters")
    private String address;

    @NotBlank(message = "Store login username is required")
    @Size(min = 4, max = 60, message = "Store login username must be 4 to 60 characters")
    private String operatorUsername;

    @NotBlank(message = "Store login password is required")
    @Size(min = 6, max = 60, message = "Store login password must be 6 to 60 characters")
    private String operatorPassword;
}
