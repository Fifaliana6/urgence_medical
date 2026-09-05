// repository/PatientRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByNomContainingIgnoreCase(String nom);
}