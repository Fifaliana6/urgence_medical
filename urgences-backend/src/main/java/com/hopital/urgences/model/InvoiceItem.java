// model/InvoiceItem.java
package com.hopital.urgences.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    private String description;
    private Integer quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    private BigDecimal montant;
    private String type;
}