// model/Doctor.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    @Enumerated(EnumType.STRING)
    private DoctorStatus statut;
    private String telephone;
}