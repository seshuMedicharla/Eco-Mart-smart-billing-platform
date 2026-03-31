package com.ecowaste.smartbilling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorePasswordResetRequest {

    @NotBlank(message = "Platform admin username is required")
    private String platformUsername;

    @NotBlank(message = "Platform admin password is required")
    private String platformPassword;

    @NotBlank(message = "Store login username is required")
    @Size(min = 4, max = 60, message = "Store login username must be 4 to 60 characters")
    private String operatorUsername;

    @NotBlank(message = "New store login password is required")
    @Size(min = 6, max = 60, message = "New store login password must be 6 to 60 characters")
    private String newPassword;
}
