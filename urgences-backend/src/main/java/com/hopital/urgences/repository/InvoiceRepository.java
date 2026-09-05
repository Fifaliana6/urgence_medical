// repository/InvoiceRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByVisiteId(Long visiteId);
}