package com.hopital.urgences.controller;

import com.hopital.urgences.model.Exam;
import com.hopital.urgences.repository.ExamRepository;
import com.hopital.urgences.service.ExamService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamRepository examRepository;
    private final ExamService examService;

    @GetMapping("/{id}")
    public Exam obtenir(@PathVariable Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Examen introuvable: " + id));
    }

    @GetMapping("/consultation/{consultationId}")
    public List<Exam> parConsultation(@PathVariable Long consultationId) {
        return examRepository.findByConsultationId(consultationId);
    }

    @PutMapping("/{id}/resultat")
    public Exam saisirResultat(@PathVariable Long id, @RequestBody String resultat) {
        return examService.saisirResultat(id, resultat);
    }
}