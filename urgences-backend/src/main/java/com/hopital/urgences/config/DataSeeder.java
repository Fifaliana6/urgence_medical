package com.hopital.urgences.config;

import com.hopital.urgences.model.*;
import com.hopital.urgences.model.salle.Lit;
import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.salle.TypeSalle;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.repository.LitRepository;
import com.hopital.urgences.repository.SalleRepository;
import com.hopital.urgences.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SalleRepository salleRepository;
    private final LitRepository litRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            creerUtilisateur("Admin Principal", "admin@urgences.mg", Role.ADMIN);
            creerUtilisateur("Réception Accueil", "reception@urgences.mg", Role.RECEPTIONIST);
            creerUtilisateur("Dr. Rakoto", "medecin1@urgences.mg", Role.MEDECIN);
            creerUtilisateur("Dr. Rasoa", "medecin2@urgences.mg", Role.MEDECIN);
            creerUtilisateur("Labo Central", "labo@urgences.mg", Role.LABO_IMAGERIE);
        }

        if (salleRepository.count() == 0) {
            salleRepository.save(Salle.builder().nom("Salle de déchocage 1").type(TypeSalle.DECHOCAGE).disponible(true).build());
            salleRepository.save(Salle.builder().nom("Salle de déchocage 2").type(TypeSalle.DECHOCAGE).disponible(true).build());
            salleRepository.save(Salle.builder().nom("Salle de consultation 1").type(TypeSalle.CONSULTATION).disponible(true).build());
            salleRepository.save(Salle.builder().nom("Salle d'imagerie 1").type(TypeSalle.IMAGERIE).disponible(true).build());
        }

        if (litRepository.count() == 0) {
            for (int i = 1; i <= 3; i++) {
                litRepository.save(Lit.builder().numero("CARD-0" + i).service(ServiceMedical.CARDIOLOGIE).occupe(false).build());
                litRepository.save(Lit.builder().numero("REA-0" + i).service(ServiceMedical.REANIMATION).occupe(false).build());
                litRepository.save(Lit.builder().numero("MG-0" + i).service(ServiceMedical.MEDECINE_GENERALE).occupe(false).build());
            }
        }
    }

    private void creerUtilisateur(String nom, String email, Role role) {
        userRepository.save(User.builder()
                .nom(nom)
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .role(role)
                .statut(AccountStatus.APPROVED)
                .disponible(true)
                .build());
    }
}