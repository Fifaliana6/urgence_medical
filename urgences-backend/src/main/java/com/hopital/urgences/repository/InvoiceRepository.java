package com.hopital.urgences.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopital.urgences.model.invoice.Invoice;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByVisitId(Long visitId);
}