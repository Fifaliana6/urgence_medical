// model/Bed.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bed")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bed {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numero;
    private String service;
    private boolean occupe;
}