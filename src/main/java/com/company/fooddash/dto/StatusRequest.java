package com.company.fooddash.dto;

import com.company.fooddash.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull(message = "Status is required") OrderStatus status) {
}
