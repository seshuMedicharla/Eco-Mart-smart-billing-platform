package com.ecowaste.smartbilling.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BillSaveRequest {

    @NotNull(message = "Customer details are required")
    @Valid
    private CustomerInfoRequest customer;

    @NotNull(message = "Selected items are required")
    @NotEmpty(message = "At least one product must be selected")
    @Valid
    private List<BillItemRequest> items;
}
