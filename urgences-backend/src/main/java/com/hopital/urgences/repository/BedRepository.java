// repository/BedRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByServiceAndOccupeFalse(String service);
}