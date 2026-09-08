package com.hopital.urgences.model.audit;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hopital.urgences.model.user_role.Role;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String utilisateurNom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role utilisateurRole;

    @Column(nullable = false)
    private String action;

    private Long entiteId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime dateAction;
}