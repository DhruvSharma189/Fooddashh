package com.company.fooddash.repository;

import com.company.fooddash.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {
    List<AuditEvent> findTop100ByOrderByCreatedAtDesc();
}
