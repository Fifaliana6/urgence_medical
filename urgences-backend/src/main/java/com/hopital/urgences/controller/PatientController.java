package com.hopital.urgences.controller;

import com.hopital.urgences.dto.patient.PatientCreateRequest;
import com.hopital.urgences.dto.patient.PatientDTO;
import com.hopital.urgences.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MEDECIN')")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public List<PatientDTO> rechercher(@RequestParam(required = false) String q) {
        return patientService.rechercher(q);
    }

    @GetMapping("/{id}")
    public PatientDTO getById(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public PatientDTO creer(@Valid @RequestBody PatientCreateRequest request) {
        return patientService.creer(request);
    }
}