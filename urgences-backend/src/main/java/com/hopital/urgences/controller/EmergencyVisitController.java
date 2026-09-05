package com.hopital.urgences.controller;

import com.hopital.urgences.dto.DischargeRequest;
import com.hopital.urgences.dto.TriageRequest;
import com.hopital.urgences.model.EmergencyVisit;
import com.hopital.urgences.model.VisitStatus;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.service.DischargeService;
import com.hopital.urgences.service.EmergencyVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class EmergencyVisitController {

    private final EmergencyVisitService visitService;
    private final DischargeService dischargeService;
    private final EmergencyVisitRepository visitRepository;

    @PostMapping
    public ResponseEntity<EmergencyVisit> creerVisite(@RequestBody @Valid TriageRequest requete) {
        EmergencyVisit visite = visitService.createVisit(
                requete.patientId(), requete.symptomes(), requete.niveauUrgence());
        return ResponseEntity.status(HttpStatus.CREATED).body(visite);
    }

    @GetMapping
    public List<EmergencyVisit> listerAttente() {
        return visitRepository.findByStatutOrderByNiveauUrgenceAscDateArriveeAsc(VisitStatus.EN_ATTENTE);
    }

    @GetMapping("/{id}")
    public EmergencyVisit obtenir(@PathVariable Long id) {
        return visitRepository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}/decision")
    public EmergencyVisit decider(@PathVariable Long id, @RequestBody @Valid DischargeRequest requete) {
        return dischargeService.decide(id, requete);
    }
}