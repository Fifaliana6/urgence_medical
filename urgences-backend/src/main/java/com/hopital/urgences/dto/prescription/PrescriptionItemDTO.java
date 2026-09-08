package com.hopital.urgences.dto.prescription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemDTO {
    private Long id;
    private String medicament;
    private String dosage;
    private String duree;
    private String instructions;
}