// model/Exam.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    private String type;

    @Enumerated(EnumType.STRING)
    private ExamStatus statut;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    @Column(name = "date_resultat")
    private LocalDateTime dateResultat;

    @Column(columnDefinition = "TEXT")
    private String resultat;
}