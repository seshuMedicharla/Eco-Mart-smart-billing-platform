package com.ecowaste.smartbilling.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BillRequest {
    @NotEmpty(message = "At least one product must be selected")
    @Valid
    private List<BillItemRequest> items;
}
