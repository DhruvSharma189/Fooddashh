package com.company.fooddash.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

public record MenuItemRequest(
                @NotBlank(message = "Name is required") @Size(min = 2, max = 100, message = "Name must be 2 to 100 characters") String name,
                @NotBlank(message = "Description is required") @Size(max = 300, message = "Description can be at most 300 characters") String description,
                @NotBlank(message = "Category is required") String category,
                @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") double price,
                boolean veg,
                @NotBlank(message = "Image URL is required") @URL(message = "Image URL must be a valid URL") String imageUrl,
                @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5") @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5") double rating,
                Boolean available, @Min(value = 0, message = "Stock cannot be negative") Integer stockQuantity) {
}
