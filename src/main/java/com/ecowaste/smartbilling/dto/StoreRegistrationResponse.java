package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreRegistrationResponse {
    private boolean success;
    private String message;
    private boolean registered;
    private Long storeId;
    private String storeName;
    private String operatorUsername;
    private String operatorPassword;
    private Long totalStores;
}
