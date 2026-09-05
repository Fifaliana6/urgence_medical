// repository/PrescriptionRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}