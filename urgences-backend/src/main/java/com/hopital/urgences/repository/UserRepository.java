package com.hopital.urgences.repository;

import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndDisponibleTrue(Role role);
    List<User> findByStatut(AccountStatus statut);
    List<User> findByRoleAndStatutAndDisponibleTrue(Role role, AccountStatus statut);
}