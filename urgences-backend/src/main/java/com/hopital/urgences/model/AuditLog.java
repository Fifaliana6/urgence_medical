// model/AuditLog.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String utilisateur;
    private String action;
    private String entite;

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(name = "date_heure")
    private LocalDateTime dateHeure;

    @Column(columnDefinition = "TEXT")
    private String details;
}