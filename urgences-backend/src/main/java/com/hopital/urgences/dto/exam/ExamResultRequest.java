package com.hopital.urgences.dto.exam;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamResultRequest {
    @NotBlank
    private String resultat;
}