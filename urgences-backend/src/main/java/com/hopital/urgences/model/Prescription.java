// model/Prescription.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    private String medicament;
    private String dosage;
    private String duree;

    @Column(columnDefinition = "TEXT")
    private String instructions;
}