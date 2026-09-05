// dto/PrescriptionRequest.java
package com.hopital.urgences.dto;

import jakarta.validation.constraints.NotBlank;

public record PrescriptionRequest(
        @NotBlank String medicament,
        String dosage,
        String duree,
        String instructions
) {}