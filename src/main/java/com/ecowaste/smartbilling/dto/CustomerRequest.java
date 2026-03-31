package com.ecowaste.smartbilling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
}
