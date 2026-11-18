package com.company.fooddash.dto;

import java.util.Set;

/** token = short-lived access JWT (kept as "token" so the admin pages keep working). */
public record AuthResponse(String token, String refreshToken, String tokenType, long expiresIn, String name,
        String email, Set<String> roles) {
}
