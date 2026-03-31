package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductImageUploadResponse {
    private boolean success;
    private String message;
    private String imagePath;
    private String fileName;
}
