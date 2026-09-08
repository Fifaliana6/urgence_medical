package com.hopital.urgences.dto.visite;

import com.hopital.urgences.model.UrgencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisitCreateRequest {
    @NotNull
    private Long patientId;

    @NotBlank
    private String symptomes;

    @NotNull
    private UrgencyLevel niveauUrgence;
}