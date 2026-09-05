// repository/EmergencyVisitRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.EmergencyVisit;
import com.hopital.urgences.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyVisitRepository extends JpaRepository<EmergencyVisit, Long> {
    List<EmergencyVisit> findByStatutOrderByNiveauUrgenceAscDateArriveeAsc(VisitStatus statut);
}