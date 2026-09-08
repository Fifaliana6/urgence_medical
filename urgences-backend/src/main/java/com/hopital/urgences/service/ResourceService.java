package com.hopital.urgences.service;

import com.hopital.urgences.dto.*;
import com.hopital.urgences.dto.salle_lit.LitCreateRequest;
import com.hopital.urgences.dto.salle_lit.LitDTO;
import com.hopital.urgences.dto.salle_lit.SalleCreateRequest;
import com.hopital.urgences.dto.salle_lit.SalleDTO;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.ServiceMedical;
import com.hopital.urgences.model.salle.Lit;
import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.salle.TypeSalle;
import com.hopital.urgences.repository.LitRepository;
import com.hopital.urgences.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final SalleRepository salleRepository;
    private final LitRepository litRepository;

    @Transactional(readOnly = true)
    public List<SalleDTO> listerSalles() {
        return salleRepository.findAll().stream().map(this::toSalleDTO).toList();
    }

    @Transactional
    public SalleDTO creerSalle(SalleCreateRequest request) {
        Salle salle = Salle.builder().nom(request.getNom()).type(request.getType()).disponible(true).build();
        return toSalleDTO(salleRepository.save(salle));
    }

    @Transactional
    public SalleDTO changerDisponibiliteSalle(Long id, boolean disponible) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle introuvable, id=" + id));
        salle.setDisponible(disponible);
        return toSalleDTO(salleRepository.save(salle));
    }

    @Transactional(readOnly = true)
    public List<LitDTO> listerLits() {
        return litRepository.findAll().stream().map(this::toLitDTO).toList();
    }

    @Transactional
    public LitDTO creerLit(LitCreateRequest request) {
        Lit lit = Lit.builder().numero(request.getNumero()).service(request.getService()).occupe(false).build();
        return toLitDTO(litRepository.save(lit));
    }

    public Salle trouverSalleDisponible(TypeSalle type) {
        return salleRepository.findFirstByTypeAndDisponibleTrue(type).orElse(null);
    }

    public Lit trouverLitDisponible(ServiceMedical service) {
        return litRepository.findFirstByServiceAndOccupeFalse(service).orElse(null);
    }

    public Lit trouverLitParId(Long id) {
        return litRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lit introuvable, id=" + id));
    }

    @Transactional
    public LitDTO occuperLit(Long id, boolean occupe) {
        Lit lit = trouverLitParId(id);
        lit.setOccupe(occupe);
        return toLitDTO(litRepository.save(lit));
    }

    private SalleDTO toSalleDTO(Salle s) {
        return new SalleDTO(s.getId(), s.getNom(), s.getType(), s.isDisponible());
    }

    private LitDTO toLitDTO(Lit l) {
        return new LitDTO(l.getId(), l.getNumero(), l.getService(), l.isOccupe());
    }
}