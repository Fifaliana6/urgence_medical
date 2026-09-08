package com.hopital.urgences.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.model.visite.VisitStatus;

import java.util.List;

public interface EmergencyVisitRepository extends JpaRepository<EmergencyVisit, Long> {
    List<EmergencyVisit> findByStatusOrderByNiveauUrgenceAscHeureArriveeAsc(VisitStatus status);
    List<EmergencyVisit> findByStatus(VisitStatus status);
    List<EmergencyVisit> findAllByOrderByHeureArriveeDesc();
    List<EmergencyVisit> findByStatusOrderByHeureArriveeDesc(VisitStatus status);
    long countByMedecinIdAndStatusIn(Long medecinId, List<VisitStatus> statuses);
}