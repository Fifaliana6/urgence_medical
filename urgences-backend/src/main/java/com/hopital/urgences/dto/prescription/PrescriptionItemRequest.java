package com.hopital.urgences.dto.prescription;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrescriptionItemRequest {
    @NotBlank
    private String medicament;
    @NotBlank
    private String dosage;
    private String duree;
    private String instructions;
}