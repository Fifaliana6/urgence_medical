package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 1)
    private String sexe;

    private String telephone;
    private String adresse;

    @Column(name = "numero_securite_sociale", unique = true)
    private String numeroSecuriteSociale;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EmergencyVisit> visites = new ArrayList<>();
}