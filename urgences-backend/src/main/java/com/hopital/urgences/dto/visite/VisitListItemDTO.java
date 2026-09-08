package com.hopital.urgences.dto.visite;

import com.hopital.urgences.model.UrgencyLevel;
import com.hopital.urgences.model.visite.VisitStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VisitListItemDTO {
    private Long id;
    private Long patientId;
    private String patientNomComplet;
    private UrgencyLevel niveauUrgence;
    private VisitStatus status;
    private LocalDateTime heureArrivee;
    private String medecinNom;
    private String salleAffectee;
    private String litAffecte;
}