// repository/AuditLogRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntiteOrderByDateHeureDesc(String entite);
}