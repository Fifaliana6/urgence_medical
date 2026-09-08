package com.hopital.urgences.controller;

import com.hopital.urgences.dto.*;
import com.hopital.urgences.dto.visite.VisitCreateRequest;
import com.hopital.urgences.dto.visite.VisitDetailDTO;
import com.hopital.urgences.dto.visite.VisitListItemDTO;
import com.hopital.urgences.model.visite.VisitStatus;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MEDECIN','LABO_IMAGERIE')")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public VisitDetailDTO creer(@Valid @RequestBody VisitCreateRequest request,
                                 @AuthenticationPrincipal CustomUserDetails auteur) {
        return visitService.creerVisite(request, auteur);
    }

    @GetMapping("/attente")
    public List<VisitListItemDTO> listeAttente() {
        return visitService.listeAttente();
    }

    @GetMapping
    public List<VisitListItemDTO> toutes(@RequestParam(required = false) VisitStatus statut) {
        return visitService.listerToutes(statut);
    }

    @GetMapping("/{id}")
    public VisitDetailDTO getDetail(@PathVariable Long id) {
        return visitService.getDetail(id);
    }

    @PutMapping("/{id}/sortie")
    @PreAuthorize("hasRole('MEDECIN')")
    public VisitDetailDTO sortir(@PathVariable Long id, @RequestBody(required = false) DischargeRequest request,
                                  @AuthenticationPrincipal CustomUserDetails auteur) {
        return visitService.sortirPatient(id, request != null ? request : new DischargeRequest(), auteur);
    }

    @PutMapping("/{id}/hospitalisation")
    @PreAuthorize("hasRole('MEDECIN')")
    public VisitDetailDTO hospitaliser(@PathVariable Long id, @Valid @RequestBody HospitalizeRequest request,
                                        @AuthenticationPrincipal CustomUserDetails auteur) {
        return visitService.hospitaliserPatient(id, request, auteur);
    }

    @PutMapping("/{id}/fin-hospitalisation")
    @PreAuthorize("hasRole('MEDECIN')")
    public VisitDetailDTO finHospitalisation(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auteur) {
        return visitService.finHospitalisation(id, auteur);
    }
}