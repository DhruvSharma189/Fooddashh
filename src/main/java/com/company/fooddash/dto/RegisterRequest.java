package com.company.fooddash.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(@NotBlank @Size(min = 2, max = 80) String name, @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password) {
}
