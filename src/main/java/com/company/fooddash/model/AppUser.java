package com.company.fooddash.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "app_users")
public record AppUser(@Id String id, String name, String email, String passwordHash, Set<String> roles, boolean enabled,
        LocalDateTime createdAt) {
}
