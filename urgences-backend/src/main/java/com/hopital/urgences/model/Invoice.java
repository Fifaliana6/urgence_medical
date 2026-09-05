// model/Invoice.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visite_id", nullable = false, unique = true)
    private EmergencyVisit visite;

    @Column(name = "date_emission")
    private LocalDateTime dateEmission;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus statut;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InvoiceItem> lignes = new ArrayList<>();
}