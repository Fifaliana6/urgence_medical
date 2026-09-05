package com.hopital.urgences.controller;

import com.hopital.urgences.dto.ConsultationRequest;
import com.hopital.urgences.dto.ExamRequest;
import com.hopital.urgences.dto.PrescriptionRequest;
import com.hopital.urgences.model.Consultation;
import com.hopital.urgences.model.Exam;
import com.hopital.urgences.model.Prescription;
import com.hopital.urgences.repository.ConsultationRepository;
import com.hopital.urgences.service.ConsultationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;
    private final ConsultationRepository consultationRepository;

    @PostMapping
    public ResponseEntity<Consultation> creer(@RequestBody @Valid ConsultationRequest requete) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationService.create(requete));
    }

    @GetMapping("/{id}")
    public Consultation obtenir(@PathVariable Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable: " + id));
    }

    @GetMapping("/visite/{visitId}")
    public List<Consultation> parVisite(@PathVariable Long visitId) {
        return consultationRepository.findByVisiteId(visitId);
    }

    @PostMapping("/{id}/prescriptions")
    public ResponseEntity<Prescription> ajouterPrescription(
            @PathVariable Long id, @RequestBody @Valid PrescriptionRequest requete) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationService.addPrescription(id, requete));
    }

    @PostMapping("/{id}/exams")
    public ResponseEntity<Exam> ajouterExamen(
            @PathVariable Long id, @RequestBody @Valid ExamRequest requete) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationService.addExam(id, requete));
    }
}