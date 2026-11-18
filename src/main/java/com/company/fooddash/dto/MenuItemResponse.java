package com.company.fooddash.dto;

import java.time.LocalDateTime;

public record MenuItemResponse(String id, String name, String description, String category, double price,
        boolean veg, String imageUrl, double rating, boolean available, LocalDateTime createdAt,
        Integer stockQuantity) {
}
