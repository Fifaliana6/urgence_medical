package com.hopital.urgences.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisponibiliteRequest {
    @NotNull
    private Boolean disponible;
}