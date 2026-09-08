package com.hopital.urgences.service;

import com.hopital.urgences.dto.AuditLogDTO;
import com.hopital.urgences.model.audit.AuditLog;
import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void enregistrer(User utilisateur, String action, Long entiteId, String details) {
        AuditLog log = AuditLog.builder()
                .utilisateurNom(utilisateur.getNom())
                .utilisateurRole(utilisateur.getRole())
                .action(action)
                .entiteId(entiteId)
                .details(details)
                .dateAction(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDTO> lister(int page, int taille) {
        return auditLogRepository.findAllByOrderByDateActionDesc(PageRequest.of(page, taille))
                .map(l -> new AuditLogDTO(l.getId(), l.getUtilisateurNom(), l.getUtilisateurRole().name(),
                        l.getAction(), l.getEntiteId(), l.getDetails(), l.getDateAction()));
    }
}