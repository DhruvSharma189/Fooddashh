package com.company.fooddash.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "menu_items")
public record MenuItem(@Id String id, String name, String description, String category, double price,
                       boolean veg, String imageUrl, double rating, boolean available, LocalDateTime createdAt, Integer stockQuantity) {
}
