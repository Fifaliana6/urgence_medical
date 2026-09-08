package com.hopital.urgences.model.salle;

import com.hopital.urgences.model.ServiceMedical;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceMedical service;

    @Builder.Default
    @Column(nullable = false)
    private boolean occupe = false;
}