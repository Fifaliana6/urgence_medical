package com.hopital.urgences.service;

import com.hopital.urgences.dto.DischargeRequest;
import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.BedRepository;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DischargeService {

    private final EmergencyVisitRepository visitRepository;
    private final BedRepository bedRepository;
    private final InvoiceService invoiceService;
    private final AuditService auditService;

    @Transactional
    public EmergencyVisit decide(Long visitId, DischargeRequest requete) {
        if (requete.statutFinal() != VisitStatus.DISCHARGED && requete.statutFinal() != VisitStatus.HOSPITALIZED) {
            throw new IllegalArgumentException("Le statut final doit être DISCHARGED ou HOSPITALIZED");
        }

        EmergencyVisit visite = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visite introuvable: " + visitId));

        if (requete.statutFinal() == VisitStatus.HOSPITALIZED) {
            if (requete.serviceHospitalisation() == null) {
                throw new IllegalArgumentException("Le service d'hospitalisation est requis");
            }
            List<Bed> litsLibres = bedRepository.findByServiceAndOccupeFalse(requete.serviceHospitalisation());
            if (litsLibres.isEmpty()) {
                throw new IllegalStateException("Aucun lit disponible en " + requete.serviceHospitalisation());
            }
            Bed lit = litsLibres.get(0);
            lit.setOccupe(true);
            bedRepository.save(lit);
            visite.setLit(lit);
            visite.setStatut(VisitStatus.HOSPITALIZED);
        } else {
            visite.setStatut(VisitStatus.DISCHARGED);
            visite.setDateSortie(LocalDateTime.now());
        }

        // Libère les ressources utilisées pendant le passage aux urgences
        if (visite.getMedecin() != null) {
            visite.getMedecin().setStatut(DoctorStatus.DISPONIBLE);
        }
        if (visite.getSalle() != null) {
            visite.getSalle().setDisponible(true);
        }

        visitRepository.save(visite);
        invoiceService.generateInvoice(visite);
        auditService.log("MEDECIN", "UPDATE", "EmergencyVisit", visite.getId(),
                "Décision de sortie : " + requete.statutFinal());

        return visite;
    }
}