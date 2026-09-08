package com.hopital.urgences.repository;

import com.hopital.urgences.model.exam.Exam;
import com.hopital.urgences.model.exam.ExamStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStatut(ExamStatus statut);
}