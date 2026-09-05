package com.hopital.urgences.service;

import com.hopital.urgences.jms.ExamResultProducer;
import com.hopital.urgences.model.Exam;
import com.hopital.urgences.model.ExamStatus;
import com.hopital.urgences.repository.ExamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamResultProducer examResultProducer;

    @Transactional
    public Exam saisirResultat(Long examId, String resultat) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Examen introuvable: " + examId));
        exam.setResultat(resultat);
        exam.setStatut(ExamStatus.RESULTAT_DISPONIBLE);
        exam.setDateResultat(LocalDateTime.now());
        exam = examRepository.save(exam);

        try {
            examResultProducer.publishResultAvailable(exam);
        } catch (Exception e) {
            log.error("Échec de la notification JMS pour l'examen {} : {}", exam.getId(), e.getMessage(), e);
        }

        return exam;
    }
}