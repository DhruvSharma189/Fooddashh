package com.company.fooddash.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/** Only the SHA-256 hash of the refresh token is stored, never the token itself. */
@Document(collection = "refresh_tokens")
public record RefreshToken(@Id String id, String tokenHash, String email, Instant expiresAt) {
}
