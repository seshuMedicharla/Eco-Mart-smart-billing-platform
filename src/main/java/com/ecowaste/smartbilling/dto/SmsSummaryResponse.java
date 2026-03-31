package com.ecowaste.smartbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SmsSummaryResponse {
    private String recipientName;
    private String messageText;
}
