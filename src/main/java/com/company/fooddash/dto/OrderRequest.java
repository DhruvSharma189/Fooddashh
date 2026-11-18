package com.company.fooddash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OrderRequest(
        @NotBlank(message = "Name is required") @Size(min = 2, max = 60, message = "Name must be 2 to 60 characters") String customerName,
        @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone,
        @NotBlank(message = "Address is required") @Size(min = 5, max = 200, message = "Address must be 5 to 200 characters") String address,
        @NotEmpty(message = "Cart is empty") @Valid List<Line> items) {

    public record Line(@NotBlank(message = "Item id is required") String menuItemId,
            @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 20, message = "Quantity cannot exceed 20") int quantity) {
    }
}
