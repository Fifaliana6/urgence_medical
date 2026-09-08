package com.hopital.urgences.dto.salle_lit;

import com.hopital.urgences.model.ServiceMedical;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LitCreateRequest {
    @NotBlank
    private String numero;
    @NotNull
    private ServiceMedical service;
}