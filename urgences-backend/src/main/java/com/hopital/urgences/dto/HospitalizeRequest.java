package com.hopital.urgences.dto;

import com.hopital.urgences.model.ServiceMedical;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalizeRequest {
    @NotNull
    private ServiceMedical service;

    private Long litId;
}