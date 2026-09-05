// dto/TriageRequest.java
package com.hopital.urgences.dto;

import com.hopital.urgences.model.UrgencyLevel;
import jakarta.validation.constraints.NotNull;

public record TriageRequest(
        @NotNull Long patientId,
        String symptomes,
        @NotNull UrgencyLevel niveauUrgence
) {}