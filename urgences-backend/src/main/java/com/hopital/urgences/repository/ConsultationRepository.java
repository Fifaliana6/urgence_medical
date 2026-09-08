package com.hopital.urgences.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopital.urgences.model.visite.Consultation;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByVisitId(Long visitId);
}