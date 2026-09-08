package com.hopital.urgences.model.salle;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSalle type;

    @Builder.Default
    @Column(nullable = false)
    private boolean disponible = true;
}