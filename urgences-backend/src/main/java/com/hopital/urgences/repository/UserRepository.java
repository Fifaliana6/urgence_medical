// repository/UserRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}