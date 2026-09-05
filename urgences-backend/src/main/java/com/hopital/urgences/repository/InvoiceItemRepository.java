// repository/InvoiceItemRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}