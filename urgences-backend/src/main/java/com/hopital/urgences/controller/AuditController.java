package com.hopital.urgences.controller;

import com.hopital.urgences.dto.AuditLogDTO;
import com.hopital.urgences.service.AuditService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public Page<AuditLogDTO> lister(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int taille) {
        return auditService.lister(page, taille);
    }
}