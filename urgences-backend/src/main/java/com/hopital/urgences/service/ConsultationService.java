package com.hopital.urgences.service;

import com.hopital.urgences.dto.ConsultationRequest;
import com.hopital.urgences.dto.ExamRequest;
import com.hopital.urgences.dto.PrescriptionRequest;
import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final EmergencyVisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ExamRepository examRepository;
    private final AuditService auditService;

    @Transactional
    public Consultation create(ConsultationRequest requete) {
        EmergencyVisit visite = visitRepository.findById(requete.visitId())
                .orElseThrow(() -> new EntityNotFoundException("Visite introuvable: " + requete.visitId()));
        Doctor medecin = doctorRepository.findById(requete.medecinId())
                .orElseThrow(() -> new EntityNotFoundException("Médecin introuvable: " + requete.medecinId()));

        Consultation consultation = Consultation.builder()
                .visite(visite)
                .medecin(medecin)
                .dateConsultation(LocalDateTime.now())
                .diagnostic(requete.diagnostic())
                .planTraitement(requete.planTraitement())
                .build();
        consultation = consultationRepository.save(consultation);

        if (visite.getMedecin() == null) {
            visite.setMedecin(medecin);
        }
        visite.setStatut(VisitStatus.EN_CONSULTATION);
        visitRepository.save(visite);

        auditService.log(medecin.getNom(), "CREATE", "Consultation", consultation.getId(), "Diagnostic saisi");
        return consultation;
    }

    @Transactional
    public Prescription addPrescription(Long consultationId, PrescriptionRequest requete) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable: " + consultationId));

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .medicament(requete.medicament())
                .dosage(requete.dosage())
                .duree(requete.duree())
                .instructions(requete.instructions())
                .build();
        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public Exam addExam(Long consultationId, ExamRequest requete) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable: " + consultationId));

        Exam exam = Exam.builder()
                .consultation(consultation)
                .type(requete.type())
                .statut(ExamStatus.PRESCRIT)
                .dateDemande(LocalDateTime.now())
                .build();
        exam = examRepository.save(exam);

        EmergencyVisit visite = consultation.getVisite();
        visite.setStatut(VisitStatus.EN_EXAMEN);
        visitRepository.save(visite);

        return exam;
    }
}