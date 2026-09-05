package com.hopital.urgences.jms;

import com.hopital.urgences.model.UrgencyLevel;
import java.time.LocalDateTime;

public record UrgentCaseEvent(
        Long visitId,
        Long patientId,
        String symptomes,
        UrgencyLevel niveauUrgence,
        LocalDateTime dateArrivee
) {}