package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role destinataireRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_user_id")
    private User destinataireUser;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private Long visitId;

    @Builder.Default
    private boolean lu = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation;
}