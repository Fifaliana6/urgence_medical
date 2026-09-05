// jms/ExamResultEvent.java
package com.hopital.urgences.jms;

public record ExamResultEvent(
        Long examId,
        Long consultationId,
        Long visitId,
        String type,
        String resultat
) {}