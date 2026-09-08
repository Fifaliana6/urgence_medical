package com.hopital.urgences.dto.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientCreateRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
}