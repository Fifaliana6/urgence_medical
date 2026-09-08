package com.hopital.urgences.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hopital.urgences.model.prescription.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}