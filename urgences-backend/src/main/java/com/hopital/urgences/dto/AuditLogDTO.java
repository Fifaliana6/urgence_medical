package com.hopital.urgences.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String utilisateurNom;
    private String utilisateurRole;
    private String action;
    private Long entiteId;
    private String details;
    private LocalDateTime dateAction;
}