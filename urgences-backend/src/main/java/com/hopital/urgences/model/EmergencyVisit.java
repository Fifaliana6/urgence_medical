package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emergency_visit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "date_arrivee", nullable = false)
    private LocalDateTime dateArrivee;

    @Column(columnDefinition = "TEXT")
    private String symptomes;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_urgence", nullable = false)
    private UrgencyLevel niveauUrgence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id")
    private Doctor medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Room salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_id")
    private Bed lit;

    @Column(name = "date_sortie")
    private LocalDateTime dateSortie;

    @OneToMany(mappedBy = "visite", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Consultation> consultations = new ArrayList<>();
}