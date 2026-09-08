package com.hopital.urgences.service;

import com.hopital.urgences.dto.patient.PatientCreateRequest;
import com.hopital.urgences.dto.patient.PatientDTO;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.Patient;
import com.hopital.urgences.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientDTO> rechercher(String terme) {
        List<Patient> patients = (terme == null || terme.isBlank())
                ? patientRepository.findAll()
                : patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(terme, terme);
        return patients.stream().map(this::toDTO).toList();
    }

    public PatientDTO getById(Long id) {
        return toDTO(findEntity(id));
    }

    public Patient findEntity(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable, id=" + id));
    }

    public PatientDTO creer(PatientCreateRequest request) {
        Patient patient = Patient.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .build();
        return toDTO(patientRepository.save(patient));
    }

    private PatientDTO toDTO(Patient p) {
        return new PatientDTO(p.getId(), p.getNom(), p.getPrenom(), p.getDateNaissance(), p.getTelephone(), p.getAdresse());
    }
}