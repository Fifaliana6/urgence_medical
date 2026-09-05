package com.hopital.urgences.config;

import com.hopital.urgences.model.User;
import com.hopital.urgences.model.UserRole;
import com.hopital.urgences.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .nom("Admin")
                    .prenom("Système")
                    .build());
            System.out.println(">>> Compte admin créé (admin / admin123) — à changer en production !");
        }
    }
}