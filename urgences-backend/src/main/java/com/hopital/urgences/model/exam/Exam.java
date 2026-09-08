package com.hopital.urgences.model.exam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.model.visite.Consultation;

@Entity
@Table(name = "exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType type;

    @Column(nullable = false)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus statut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demande_par_id", nullable = false)
    private User demandePar;

    @Column(nullable = false)
    private LocalDateTime dateDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realise_par_id")
    private User realisePar;

    @Column(columnDefinition = "TEXT")
    private String resultat;

    private LocalDateTime dateResultat;
}