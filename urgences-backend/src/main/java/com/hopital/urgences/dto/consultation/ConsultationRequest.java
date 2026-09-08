package com.hopital.urgences.dto.consultation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConsultationRequest {
    @NotBlank
    private String diagnostic;

    @NotBlank
    private String planTraitement;
}