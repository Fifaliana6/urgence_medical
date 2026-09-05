package com.hopital.urgences.controller;

import com.hopital.urgences.dto.PatientRequest;
import com.hopital.urgences.model.Patient;
import com.hopital.urgences.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;

    @PostMapping
    public ResponseEntity<Patient> creer(@RequestBody @Valid PatientRequest requete) {
        Patient patient = Patient.builder()
                .nom(requete.nom())
                .prenom(requete.prenom())
                .dateNaissance(requete.dateNaissance())
                .sexe(requete.sexe())
                .telephone(requete.telephone())
                .adresse(requete.adresse())
                .numeroSecuriteSociale(requete.numeroSecuriteSociale())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(patientRepository.save(patient));
    }

    @GetMapping
    public List<Patient> lister() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Patient obtenir(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable: " + id));
    }

    @GetMapping("/search")
    public List<Patient> rechercher(@RequestParam String nom) {
        return patientRepository.findByNomContainingIgnoreCase(nom);
    }

    @PutMapping("/{id}")
    public Patient modifier(@PathVariable Long id, @RequestBody @Valid PatientRequest requete) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable: " + id));
        patient.setNom(requete.nom());
        patient.setPrenom(requete.prenom());
        patient.setDateNaissance(requete.dateNaissance());
        patient.setSexe(requete.sexe());
        patient.setTelephone(requete.telephone());
        patient.setAdresse(requete.adresse());
        patient.setNumeroSecuriteSociale(requete.numeroSecuriteSociale());
        return patientRepository.save(patient);
    }
}