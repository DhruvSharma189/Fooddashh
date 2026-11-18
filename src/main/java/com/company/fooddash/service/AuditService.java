package com.company.fooddash.service;

import com.company.fooddash.model.AuditEvent;
import com.company.fooddash.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditService {
    private final AuditEventRepository repo;

    public AuditService(AuditEventRepository r) {
        repo = r;
    }

    public void record(String actor, String action, String details) {
        repo.save(new AuditEvent(null, actor, action, details, LocalDateTime.now()));
    }
}
