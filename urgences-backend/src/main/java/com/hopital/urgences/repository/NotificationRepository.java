package com.hopital.urgences.repository;

import com.hopital.urgences.model.Notification;
import com.hopital.urgences.model.user_role.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireRoleAndLuFalse(Role role);
    List<Notification> findByDestinataireUserIdAndLuFalse(Long userId);
}