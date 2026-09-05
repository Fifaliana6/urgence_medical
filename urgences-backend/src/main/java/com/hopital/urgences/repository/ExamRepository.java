// repository/ExamRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByConsultationId(Long consultationId);
}