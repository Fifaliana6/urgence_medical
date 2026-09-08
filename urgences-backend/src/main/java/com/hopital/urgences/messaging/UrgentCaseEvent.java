package com.hopital.urgences.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgentCaseEvent {
    private Long visitId;
    private Long patientId;
    private String patientNomComplet;
    private String niveauUrgence;
    private String symptomes;
}