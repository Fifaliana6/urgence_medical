// dto/ConsultationRequest.java
package com.hopital.urgences.dto;

import jakarta.validation.constraints.NotNull;

public record ConsultationRequest(
        @NotNull Long visitId,
        @NotNull Long medecinId,
        String diagnostic,
        String planTraitement
) {}