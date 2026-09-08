package com.hopital.urgences.service;

import com.hopital.urgences.dto.*;
import com.hopital.urgences.dto.consultation.ConsultationDTO;
import com.hopital.urgences.dto.exam.ExamDTO;
import com.hopital.urgences.dto.patient.PatientDTO;
import com.hopital.urgences.dto.prescription.PrescriptionDTO;
import com.hopital.urgences.dto.prescription.PrescriptionItemDTO;
import com.hopital.urgences.dto.visite.VisitCreateRequest;
import com.hopital.urgences.dto.visite.VisitDetailDTO;
import com.hopital.urgences.dto.visite.VisitListItemDTO;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.messaging.UrgentCaseEvent;
import com.hopital.urgences.messaging.UrgentCaseProducer;
import com.hopital.urgences.model.*;
import com.hopital.urgences.model.salle.Lit;
import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.visite.Consultation;
import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.model.visite.VisitStatus;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final EmergencyVisitRepository visitRepository;
    private final PatientService patientService;
    private final UrgentCaseProducer urgentCaseProducer;
    private final ResourceService resourceService;
    private final InvoiceService invoiceService;
    private final AuditService auditService;

    @Transactional
    public VisitDetailDTO creerVisite(VisitCreateRequest request, CustomUserDetails auteur) {
        Patient patient = patientService.findEntity(request.getPatientId());

        EmergencyVisit visit = EmergencyVisit.builder()
                .patient(patient)
                .symptomes(request.getSymptomes())
                .niveauUrgence(request.getNiveauUrgence())
                .heureArrivee(LocalDateTime.now())
                .status(VisitStatus.EN_ATTENTE)
                .build();

        visit = visitRepository.save(visit);

        auditService.enregistrer(auteur.getUser(), "CREATION_VISITE", visit.getId(),
                "Visite créée pour " + patient.getPrenom() + " " + patient.getNom()
                        + " — niveau " + visit.getNiveauUrgence());

        if (request.getNiveauUrgence() == UrgencyLevel.CRITIQUE) {
            UrgentCaseEvent event = new UrgentCaseEvent(
                    visit.getId(), patient.getId(),
                    patient.getPrenom() + " " + patient.getNom(),
                    visit.getNiveauUrgence().name(), visit.getSymptomes());
            urgentCaseProducer.publierUrgenceCritique(event);
        }

        return toDetailDTO(visit);
    }

    @Transactional(readOnly = true)
    public List<VisitListItemDTO> listeAttente() {
        List<EmergencyVisit> visits = visitRepository.findByStatusOrderByNiveauUrgenceAscHeureArriveeAsc(VisitStatus.EN_ATTENTE);
        return visits.stream()
                .sorted(Comparator.comparing((EmergencyVisit v) -> ordreUrgence(v.getNiveauUrgence()))
                        .thenComparing(EmergencyVisit::getHeureArrivee))
                .map(this::toListItemDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VisitListItemDTO> listerToutes(VisitStatus statut) {
        List<EmergencyVisit> visits = statut != null
                ? visitRepository.findByStatusOrderByHeureArriveeDesc(statut)
                : visitRepository.findAllByOrderByHeureArriveeDesc();
        return visits.stream().map(this::toListItemDTO).toList();
    }

    private int ordreUrgence(UrgencyLevel niveau) {
        return switch (niveau) {
            case CRITIQUE -> 0;
            case ELEVEE -> 1;
            case MOYENNE -> 2;
            case FAIBLE -> 3;
        };
    }

    @Transactional(readOnly = true)
    public VisitDetailDTO getDetail(Long id) {
        return toDetailDTO(findEntity(id));
    }

    @Transactional(readOnly = true)
    public EmergencyVisit findEntity(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visite introuvable, id=" + id));
    }

    @Transactional
    public VisitDetailDTO sortirPatient(Long visitId, DischargeRequest request, CustomUserDetails auteur) {
        EmergencyVisit visit = findEntity(visitId);
        verifierNonCloturee(visit);

        visit.setStatus(VisitStatus.DISCHARGED);
        visit.setDateSortieUrgences(LocalDateTime.now());
        libererSalle(visit);
        visitRepository.save(visit);

        auditService.enregistrer(auteur.getUser(), "SORTIE_PATIENT", visit.getId(),
                "Sortie directe validée (DISCHARGED)" + (request.getObservationsSortie() != null ? " — " + request.getObservationsSortie() : ""));

        invoiceService.genererFacture(visit);

        return toDetailDTO(visit);
    }

    @Transactional
    public VisitDetailDTO hospitaliserPatient(Long visitId, HospitalizeRequest request, CustomUserDetails auteur) {
        EmergencyVisit visit = findEntity(visitId);
        verifierNonCloturee(visit);

        Lit lit;
        if (request.getLitId() != null) {
            lit = resourceService.trouverLitParId(request.getLitId());
            if (lit.isOccupe()) {
                throw new IllegalStateException("Ce lit est déjà occupé.");
            }
            if (lit.getService() != request.getService()) {
                throw new IllegalStateException("Ce lit n'appartient pas au service sélectionné.");
            }
        } else {
            lit = resourceService.trouverLitDisponible(request.getService());
            if (lit == null) {
                throw new IllegalStateException("Aucun lit disponible dans le service " + request.getService() + ".");
            }
        }
        resourceService.occuperLit(lit.getId(), true);

        visit.setStatus(VisitStatus.HOSPITALIZED);
        visit.setLit(lit);
        visit.setDateSortieUrgences(LocalDateTime.now());
        libererSalle(visit);
        visitRepository.save(visit);

        auditService.enregistrer(auteur.getUser(), "HOSPITALISATION", visit.getId(),
                "Hospitalisation en " + lit.getService() + " — lit " + lit.getNumero());

        // Facture générée seulement à la fin réelle du séjour (finHospitalisation),
        // pour inclure les frais de séjour calculés sur la durée réelle.

        return toDetailDTO(visit);
    }

    @Transactional
    public VisitDetailDTO finHospitalisation(Long visitId, CustomUserDetails auteur) {
        EmergencyVisit visit = findEntity(visitId);

        if (visit.getStatus() != VisitStatus.HOSPITALIZED) {
            throw new IllegalStateException("Cette visite n'est pas en cours d'hospitalisation.");
        }

        visit.setDateFinHospitalisation(LocalDateTime.now());
        visit.setStatus(VisitStatus.DISCHARGED);

        if (visit.getLit() != null) {
            resourceService.occuperLit(visit.getLit().getId(), false);
        }

        visitRepository.save(visit);

        auditService.enregistrer(auteur.getUser(), "FIN_HOSPITALISATION", visit.getId(), "Fin du séjour, lit libéré");

        invoiceService.genererFacture(visit);

        return toDetailDTO(visit);
    }

    private void verifierNonCloturee(EmergencyVisit visit) {
        if (visit.getStatus() == VisitStatus.DISCHARGED || visit.getStatus() == VisitStatus.HOSPITALIZED) {
            throw new IllegalStateException("Cette visite est déjà clôturée (statut : " + visit.getStatus() + ")");
        }
    }

    private void libererSalle(EmergencyVisit visit) {
        if (visit.getSalle() != null) {
            resourceService.changerDisponibiliteSalle(visit.getSalle().getId(), true);
        }
    }

    public void assignerSalle(EmergencyVisit visit, Salle salle) {
        visit.setSalle(salle);
        visitRepository.save(visit);
    }

    public VisitDetailDTO toDetailDTO(EmergencyVisit v) {
        PatientDTO patientDTO = new PatientDTO(
                v.getPatient().getId(), v.getPatient().getNom(), v.getPatient().getPrenom(),
                v.getPatient().getDateNaissance(), v.getPatient().getTelephone(), v.getPatient().getAdresse());

        List<ConsultationDTO> consultationDTOs = v.getConsultations().stream()
                .map(this::toConsultationDTO)
                .toList();

        String salleNom = v.getSalle() != null ? v.getSalle().getNom() : null;
        String litInfo = v.getLit() != null ? "Lit " + v.getLit().getNumero() + " — " + v.getLit().getService() : null;

        return new VisitDetailDTO(
                v.getId(), patientDTO, v.getSymptomes(), v.getNiveauUrgence(), v.getStatus(),
                v.getHeureArrivee(), v.getMedecin() != null ? v.getMedecin().getNom() : null,
                salleNom, litInfo, v.getDateSortieUrgences(), v.getDateFinHospitalisation(), consultationDTOs);
    }

    private ConsultationDTO toConsultationDTO(Consultation c) {
        List<PrescriptionDTO> prescriptionDTOs = c.getPrescriptions().stream().map(p ->
                new PrescriptionDTO(p.getId(), p.getDateCreation(),
                        p.getItems().stream().map(i ->
                                new PrescriptionItemDTO(i.getId(), i.getMedicament(), i.getDosage(),
                                        i.getDuree(), i.getInstructions())
                        ).toList())
        ).toList();

        List<ExamDTO> examDTOs = c.getExams().stream().map(e ->
                new ExamDTO(e.getId(), c.getVisit().getId(), e.getType(), e.getLibelle(), e.getStatut(),
                        e.getDemandePar().getNom(), e.getDateDemande(),
                        e.getRealisePar() != null ? e.getRealisePar().getNom() : null,
                        e.getResultat(), e.getDateResultat(),
                        c.getVisit().getPatient().getPrenom() + " " + c.getVisit().getPatient().getNom())
        ).toList();

        return new ConsultationDTO(c.getId(), c.getMedecin().getNom(), c.getDiagnostic(),
                c.getPlanTraitement(), c.getDateConsultation(), prescriptionDTOs, examDTOs);
    }

    private VisitListItemDTO toListItemDTO(EmergencyVisit v) {
        return new VisitListItemDTO(v.getId(), v.getPatient().getId(),
                v.getPatient().getPrenom() + " " + v.getPatient().getNom(),
                v.getNiveauUrgence(), v.getStatus(), v.getHeureArrivee(),
                v.getMedecin() != null ? v.getMedecin().getNom() : null,
                v.getSalle() != null ? v.getSalle().getNom() : null,
                v.getLit() != null ? "Lit " + v.getLit().getNumero() + " — " + v.getLit().getService() : null);
    }
}