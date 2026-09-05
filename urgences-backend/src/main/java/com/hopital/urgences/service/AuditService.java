// service/AuditService.java
package com.hopital.urgences.service;

import com.hopital.urgences.model.AuditLog;
import com.hopital.urgences.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String utilisateur, String action, String entite, Long entiteId, String details) {
        auditLogRepository.save(AuditLog.builder()
                .utilisateur(utilisateur)
                .action(action)
                .entite(entite)
                .entiteId(entiteId)
                .dateHeure(LocalDateTime.now())
                .details(details)
                .build());
    }
}