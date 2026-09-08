package com.hopital.urgences.dto.salle_lit;

import com.hopital.urgences.model.salle.TypeSalle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalleCreateRequest {
    @NotBlank
    private String nom;
    @NotNull
    private TypeSalle type;
}