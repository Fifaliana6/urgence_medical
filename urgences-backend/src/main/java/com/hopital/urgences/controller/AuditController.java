package com.hopital.urgences.controller;

import com.hopital.urgences.model.AuditLog;
import com.hopital.urgences.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> lister() {
        return auditLogRepository.findAll();
    }

    @GetMapping("/entite/{entite}")
    public List<AuditLog> parEntite(@PathVariable String entite) {
        return auditLogRepository.findByEntiteOrderByDateHeureDesc(entite);
    }
}