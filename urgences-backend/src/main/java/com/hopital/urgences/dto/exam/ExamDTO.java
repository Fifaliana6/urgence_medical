package com.hopital.urgences.dto.exam;

import com.hopital.urgences.model.exam.ExamStatus;
import com.hopital.urgences.model.exam.ExamType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private Long id;
    private Long visitId;
    private ExamType type;
    private String libelle;
    private ExamStatus statut;
    private String demandeParNom;
    private LocalDateTime dateDemande;
    private String realiseParNom;
    private String resultat;
    private LocalDateTime dateResultat;
    private String patientNomComplet;
}