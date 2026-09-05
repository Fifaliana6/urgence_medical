// dto/DischargeRequest.java
package com.hopital.urgences.dto;

import com.hopital.urgences.model.VisitStatus;
import jakarta.validation.constraints.NotNull;

public record DischargeRequest(
        @NotNull VisitStatus statutFinal,     // DISCHARGED ou HOSPITALIZED uniquement
        String serviceHospitalisation          // requis si HOSPITALIZED, ex: "CARDIOLOGIE"
) {}