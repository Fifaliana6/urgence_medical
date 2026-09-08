package com.hopital.urgences.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.salle.TypeSalle;

import java.util.List;
import java.util.Optional;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findByTypeAndDisponibleTrue(TypeSalle type);
    Optional<Salle> findFirstByTypeAndDisponibleTrue(TypeSalle type);
}