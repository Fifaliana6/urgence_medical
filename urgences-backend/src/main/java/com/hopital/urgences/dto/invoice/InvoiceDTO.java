package com.hopital.urgences.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    private Long visitId;
    private Double montantTotal;
    private String statut;
    private LocalDateTime dateEmission;
    private List<InvoiceItemDTO> items;
}