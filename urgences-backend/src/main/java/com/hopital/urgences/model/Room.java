// model/Room.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private boolean disponible;
}