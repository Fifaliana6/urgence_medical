// model/Consultation.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Consultation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visite_id", nullable = false)
    private EmergencyVisit visite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Doctor medecin;

    @Column(name = "date_consultation")
    private LocalDateTime dateConsultation;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(name = "plan_traitement", columnDefinition = "TEXT")
    private String planTraitement;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Exam> examens = new ArrayList<>();
}