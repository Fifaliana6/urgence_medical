package com.hopital.urgences.controller;

import com.hopital.urgences.dto.consultation.ConsultationRequest;
import com.hopital.urgences.dto.exam.ExamRequest;
import com.hopital.urgences.dto.prescription.PrescriptionRequest;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEDECIN')")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping("/api/visits/{visitId}/consultations")
    public Long ouvrir(@PathVariable Long visitId, @Valid @RequestBody ConsultationRequest request,
                        @AuthenticationPrincipal CustomUserDetails medecin) {
        return consultationService.ouvrirConsultation(visitId, request, medecin).getId();
    }

    @PostMapping("/api/consultations/{consultationId}/prescriptions")
    public Long prescrire(@PathVariable Long consultationId, @Valid @RequestBody PrescriptionRequest request,
                           @AuthenticationPrincipal CustomUserDetails medecin) {
        return consultationService.ajouterPrescription(consultationId, request, medecin).getId();
    }

    @PostMapping("/api/consultations/{consultationId}/exams")
    public Long demanderExamen(@PathVariable Long consultationId, @Valid @RequestBody ExamRequest request,
                                @AuthenticationPrincipal CustomUserDetails medecin) {
        return consultationService.demanderExamen(consultationId, request, medecin).getId();
    }
}