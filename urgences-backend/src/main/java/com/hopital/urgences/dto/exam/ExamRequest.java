package com.hopital.urgences.dto.exam;

import com.hopital.urgences.model.exam.ExamType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamRequest {
    @NotNull
    private ExamType type;

    @NotBlank
    private String libelle;
}