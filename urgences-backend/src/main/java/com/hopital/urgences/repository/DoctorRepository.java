// repository/DoctorRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Doctor;
import com.hopital.urgences.model.DoctorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findFirstByStatut(DoctorStatus statut);
}