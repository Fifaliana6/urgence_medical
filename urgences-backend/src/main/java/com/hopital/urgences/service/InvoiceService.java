package com.hopital.urgences.service;

import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    // Tarifs simplifiés en dur (à remplacer par une vraie table de tarification si besoin)
    private static final BigDecimal PRIX_CONSULTATION = new BigDecimal("50.00");
    private static final BigDecimal PRIX_EXAMEN = new BigDecimal("30.00");
    private static final BigDecimal PRIX_MEDICAMENT = new BigDecimal("15.00");

    private final InvoiceRepository invoiceRepository;

    @Transactional
    public Invoice generateInvoice(EmergencyVisit visite) {
        List<InvoiceItem> lignes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Consultation c : visite.getConsultations()) {
            InvoiceItem ligneConsult = ligne("Consultation - Dr " + c.getMedecin().getNom(), 1, PRIX_CONSULTATION, "CONSULTATION");
            lignes.add(ligneConsult);
            total = total.add(ligneConsult.getMontant());

            for (Prescription p : c.getPrescriptions()) {
                InvoiceItem lignePresc = ligne("Médicament - " + p.getMedicament(), 1, PRIX_MEDICAMENT, "MEDICAMENT");
                lignes.add(lignePresc);
                total = total.add(lignePresc.getMontant());
            }

            for (Exam e : c.getExamens()) {
                InvoiceItem ligneExam = ligne("Examen - " + e.getType(), 1, PRIX_EXAMEN, "EXAMEN");
                lignes.add(ligneExam);
                total = total.add(ligneExam.getMontant());
            }
        }

        Invoice invoice = Invoice.builder()
                .visite(visite)
                .dateEmission(LocalDateTime.now())
                .montantTotal(total)
                .statut(InvoiceStatus.EN_ATTENTE)
                .build();

        lignes.forEach(l -> l.setInvoice(invoice));
        invoice.setLignes(lignes);

        return invoiceRepository.save(invoice);
    }

    private InvoiceItem ligne(String description, int quantite, BigDecimal prixUnitaire, String type) {
        return InvoiceItem.builder()
                .description(description)
                .quantite(quantite)
                .prixUnitaire(prixUnitaire)
                .montant(prixUnitaire.multiply(BigDecimal.valueOf(quantite)))
                .type(type)
                .build();
    }

    @Transactional
    public Invoice marquerPayee(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Facture introuvable: " + invoiceId));
        invoice.setStatut(InvoiceStatus.PAYEE);
        return invoiceRepository.save(invoice);
    }
}