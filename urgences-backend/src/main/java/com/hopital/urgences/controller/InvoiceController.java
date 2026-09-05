package com.hopital.urgences.controller;

import com.hopital.urgences.model.Invoice;
import com.hopital.urgences.repository.InvoiceRepository;
import com.hopital.urgences.service.InvoiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public Invoice obtenir(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable: " + id));
    }

    @GetMapping("/visite/{visitId}")
    public Invoice parVisite(@PathVariable Long visitId) {
        return invoiceRepository.findByVisiteId(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune facture pour la visite " + visitId));
    }

    @PutMapping("/{id}/payer")
    public Invoice payer(@PathVariable Long id) {
        return invoiceService.marquerPayee(id);
    }
}