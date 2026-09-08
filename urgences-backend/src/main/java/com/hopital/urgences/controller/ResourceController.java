package com.hopital.urgences.controller;

import com.hopital.urgences.dto.*;
import com.hopital.urgences.service.ResourceService;
import com.hopital.urgences.dto.salle_lit.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/salles")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MEDECIN')")
    public List<SalleDTO> listerSalles() {
        return resourceService.listerSalles();
    }

    @PostMapping("/salles")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public SalleDTO creerSalle(@Valid @RequestBody SalleCreateRequest request) {
        return resourceService.creerSalle(request);
    }

    @PutMapping("/salles/{id}/disponibilite")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public SalleDTO changerDisponibiliteSalle(@PathVariable Long id, @RequestParam boolean disponible) {
        return resourceService.changerDisponibiliteSalle(id, disponible);
    }

    @GetMapping("/lits")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MEDECIN')")
    public List<LitDTO> listerLits() {
        return resourceService.listerLits();
    }

    @PostMapping("/lits")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public LitDTO creerLit(@Valid @RequestBody LitCreateRequest request) {
        return resourceService.creerLit(request);
    }
}