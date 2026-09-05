// dto/PatientRequest.java
package com.hopital.urgences.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        LocalDate dateNaissance,
        String sexe,
        String telephone,
        String adresse,
        String numeroSecuriteSociale
) {}