package com.hopital.urgences.controller;

import com.hopital.urgences.dto.invoice.InvoiceDTO;
import com.hopital.urgences.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','MEDECIN')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/visit/{visitId}")
    public InvoiceDTO getParVisite(@PathVariable Long visitId) {
        return invoiceService.getParVisite(visitId);
    }

    @PutMapping("/{id}/paiement")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public InvoiceDTO marquerPayee(@PathVariable Long id) {
        return invoiceService.marquerPayee(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> telechargerPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.genererFacturePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}