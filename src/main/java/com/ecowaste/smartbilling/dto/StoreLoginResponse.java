package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreLoginResponse {
    private boolean success;
    private String message;
    private Long storeId;
    private String storeName;
    private String username;
}
