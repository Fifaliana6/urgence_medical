package com.hopital.urgences.model.visite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hopital.urgences.model.Patient;
import com.hopital.urgences.model.UrgencyLevel;
import com.hopital.urgences.model.salle.Lit;
import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.user_role.User;

@Entity
@Table(name = "emergency_visits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmergencyVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(columnDefinition = "TEXT")
    private String symptomes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrgencyLevel niveauUrgence;

    @Column(nullable = false)
    private LocalDateTime heureArrivee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id")
    private User medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_id")
    private Lit lit;

    private LocalDateTime dateSortieUrgences;


    private LocalDateTime dateFinHospitalisation;

    @Builder.Default
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();
}