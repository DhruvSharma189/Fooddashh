package com.company.fooddash.dto;

import jakarta.validation.constraints.*;

public record AuthRequest(@NotBlank String email, @NotBlank @Size(min = 8, max = 100) String password) {
}
