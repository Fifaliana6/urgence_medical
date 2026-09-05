// repository/ConsultationRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByVisiteId(Long visiteId);
}