package com.hopital.urgences.service;

import com.hopital.urgences.jms.EmergencyEventProducer;
import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyVisitService {

    private final EmergencyVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final EmergencyEventProducer eventProducer;
    private final AuditService auditService;

    @Transactional
    public EmergencyVisit createVisit(Long patientId, String symptomes, UrgencyLevel niveau) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable: " + patientId));

        EmergencyVisit visite = EmergencyVisit.builder()
                .patient(patient)
                .symptomes(symptomes)
                .niveauUrgence(niveau)
                .statut(VisitStatus.EN_ATTENTE)
                .dateArrivee(LocalDateTime.now())
                .build();

        visite = visitRepository.save(visite);
        auditService.log("RECEPTIONNISTE", "CREATE", "EmergencyVisit", visite.getId(), "Nouvelle admission");

        if (niveau == UrgencyLevel.CRITIQUE) {

            try {
                eventProducer.publishUrgentCase(visite);
            } catch (Exception e) {
                log.error("Échec de la publication JMS pour la visite {} : {}", visite.getId(), e.getMessage(), e);
            }
        }
        return visite;
    }
}