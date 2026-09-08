package com.hopital.urgences.repository;

import com.hopital.urgences.model.ServiceMedical;
import com.hopital.urgences.model.salle.Lit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LitRepository extends JpaRepository<Lit, Long> {
    List<Lit> findByService(ServiceMedical service);
    Optional<Lit> findFirstByServiceAndOccupeFalse(ServiceMedical service);
}