package com.company.fooddash.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "audit_events")
public record AuditEvent(@Id String id, String actor, String action, String details, LocalDateTime createdAt) { }
