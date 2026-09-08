package com.hopital.urgences.dto.consultation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

import com.hopital.urgences.dto.exam.ExamDTO;
import com.hopital.urgences.dto.prescription.PrescriptionDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long id;
    private String medecinNom;
    private String diagnostic;
    private String planTraitement;
    private LocalDateTime dateConsultation;
    private List<PrescriptionDTO> prescriptions;
    private List<ExamDTO> exams;
}