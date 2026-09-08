package com.hopital.urgences.service;

import com.hopital.urgences.dto.consultation.ConsultationRequest;
import com.hopital.urgences.dto.exam.ExamRequest;
import com.hopital.urgences.dto.prescription.PrescriptionRequest;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.exam.Exam;
import com.hopital.urgences.model.exam.ExamStatus;
import com.hopital.urgences.model.prescription.Prescription;
import com.hopital.urgences.model.prescription.PrescriptionItem;
import com.hopital.urgences.model.visite.Consultation;
import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.model.visite.VisitStatus;
import com.hopital.urgences.repository.ConsultationRepository;
import com.hopital.urgences.repository.ExamRepository;
import com.hopital.urgences.repository.PrescriptionRepository;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ExamRepository examRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final VisitService visitService;
    private final NotificationPublisher notificationPublisher;
    private final AuditService auditService;

    @Transactional
    public Consultation ouvrirConsultation(Long visitId, ConsultationRequest request, CustomUserDetails medecinAuth) {
        EmergencyVisit visit = visitService.findEntity(visitId);

        if (visit.getStatus() == VisitStatus.DISCHARGED || visit.getStatus() == VisitStatus.HOSPITALIZED) {
            throw new IllegalStateException("Impossible d'ouvrir une nouvelle consultation : la prise en charge de cette visite est déjà en cours (hospitalisée) ou terminée.");
        }

        Consultation consultation = Consultation.builder()
                .visit(visit)
                .medecin(medecinAuth.getUser())
                .diagnostic(request.getDiagnostic())
                .planTraitement(request.getPlanTraitement())
                .dateConsultation(LocalDateTime.now())
                .build();

        consultation = consultationRepository.save(consultation);

        visit.setStatus(VisitStatus.EN_CONSULTATION);
        visit.setMedecin(medecinAuth.getUser());

        auditService.enregistrer(medecinAuth.getUser(), "OUVERTURE_CONSULTATION", visitId,
                "Diagnostic posé : " + request.getDiagnostic());

        return consultation;
    }

    @Transactional
    public Prescription ajouterPrescription(Long consultationId, PrescriptionRequest request, CustomUserDetails medecinAuth) {
        Consultation consultation = findEntity(consultationId);
        verifierPriseEnChargeActive(consultation);

        Prescription nouvellePrescription = Prescription.builder()
                .consultation(consultation)
                .dateCreation(LocalDateTime.now())
                .build();

        for (var item : request.getItems()) {
            nouvellePrescription.getItems().add(
                    PrescriptionItem.builder()
                            .prescription(nouvellePrescription)
                            .medicament(item.getMedicament())
                            .dosage(item.getDosage())
                            .duree(item.getDuree())
                            .instructions(item.getInstructions())
                            .build()
            );
        }

        Prescription prescriptionEnregistree = prescriptionRepository.save(nouvellePrescription);

        auditService.enregistrer(medecinAuth.getUser(), "PRESCRIPTION", consultation.getVisit().getId(),
                request.getItems().size() + " médicament(s) prescrit(s)");

        return prescriptionEnregistree;
    }

    @Transactional
    public Exam demanderExamen(Long consultationId, ExamRequest request, CustomUserDetails medecinAuth) {
        Consultation consultation = findEntity(consultationId);
        verifierPriseEnChargeActive(consultation);

        Exam exam = Exam.builder()
                .consultation(consultation)
                .type(request.getType())
                .libelle(request.getLibelle())
                .statut(ExamStatus.EN_ATTENTE)
                .demandePar(medecinAuth.getUser())
                .dateDemande(LocalDateTime.now())
                .build();

        exam = examRepository.save(exam);

        // Un patient hospitalisé continue de recevoir des examens de suivi —
        // le statut de la visite reste HOSPITALIZED, il ne repasse en EN_EXAMEN
        // que s'il était encore dans le circuit initial des urgences.
        if (consultation.getVisit().getStatus() != VisitStatus.HOSPITALIZED) {
            consultation.getVisit().setStatus(VisitStatus.EN_EXAMEN);
        }

        notificationPublisher.notifierRole(Role.LABO_IMAGERIE,
                "Nouvel examen demandé (" + request.getType() + ") pour la visite #" + consultation.getVisit().getId(),
                consultation.getVisit().getId());

        auditService.enregistrer(medecinAuth.getUser(), "DEMANDE_EXAMEN", consultation.getVisit().getId(),
                request.getType() + " — " + request.getLibelle());

        return exam;
    }

    private void verifierPriseEnChargeActive(Consultation consultation) {
        if (consultation.getVisit().getStatus() == VisitStatus.DISCHARGED) {
            throw new IllegalStateException("Impossible d'ajouter un acte : cette visite est clôturée (patient sorti).");
        }
    }

    public Consultation findEntity(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable, id=" + id));
    }
}