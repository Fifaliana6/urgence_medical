package com.hopital.urgences.dto.visite;

import com.hopital.urgences.dto.consultation.ConsultationDTO;
import com.hopital.urgences.dto.patient.PatientDTO;
import com.hopital.urgences.model.UrgencyLevel;
import com.hopital.urgences.model.visite.VisitStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitDetailDTO {
    private Long id;
    private PatientDTO patient;
    private String symptomes;
    private UrgencyLevel niveauUrgence;
    private VisitStatus status;
    private LocalDateTime heureArrivee;
    private String medecinNom;
    private String salleAffectee;
    private String litAffecte;
    private LocalDateTime dateSortieUrgences;
    private LocalDateTime dateFinHospitalisation;
    private List<ConsultationDTO> consultations;
}