package com.hopital.urgences.service;

import com.hopital.urgences.dto.invoice.InvoiceDTO;
import com.hopital.urgences.dto.invoice.InvoiceItemDTO;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.*;
import com.hopital.urgences.model.exam.Exam;
import com.hopital.urgences.model.exam.ExamType;
import com.hopital.urgences.model.invoice.Invoice;
import com.hopital.urgences.model.invoice.InvoiceItem;
import com.hopital.urgences.model.invoice.InvoiceStatus;
import com.hopital.urgences.model.visite.Consultation;
import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final double FRAIS_CONSULTATION = 20000.0;
    private static final double FRAIS_EXAMEN_BIOLOGIE = 15000.0;
    private static final double FRAIS_EXAMEN_IMAGERIE = 35000.0;
    private static final double FRAIS_HOSPITALISATION_PAR_JOUR = 50000.0;

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional
    public InvoiceDTO genererFacture(EmergencyVisit visit) {
        List<InvoiceItem> items = new ArrayList<>();

        for (Consultation c : visit.getConsultations()) {
            items.add(InvoiceItem.builder().libelle("Consultation médicale").montant(FRAIS_CONSULTATION).build());

            for (Exam e : c.getExams()) {
                double montant = e.getType() == ExamType.IMAGERIE ? FRAIS_EXAMEN_IMAGERIE : FRAIS_EXAMEN_BIOLOGIE;
                items.add(InvoiceItem.builder().libelle("Analyse : " + e.getLibelle()).montant(montant).build());
            }
        }

        if (visit.getLit() != null && visit.getDateFinHospitalisation() != null && visit.getDateSortieUrgences() != null) {
            long jours = Math.max(1, ChronoUnit.DAYS.between(visit.getDateSortieUrgences(), visit.getDateFinHospitalisation()));
            items.add(InvoiceItem.builder()
                    .libelle("Séjour hospitalier (" + jours + (jours > 1 ? " jours" : " jour") + ")")
                    .montant(jours * FRAIS_HOSPITALISATION_PAR_JOUR)
                    .build());
        }

        double total = items.stream().mapToDouble(InvoiceItem::getMontant).sum();

        Invoice nouvelleFacture = Invoice.builder()
                .visit(visit)
                .montantTotal(total)
                .statut(InvoiceStatus.UNPAID)
                .dateEmission(LocalDateTime.now())
                .build();

        for (InvoiceItem it : items) {
            it.setInvoice(nouvelleFacture);
        }
        nouvelleFacture.setItems(items);

        Invoice factureEnregistree = invoiceRepository.save(nouvelleFacture);
        return toDTO(factureEnregistree);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getParVisite(Long visitId) {
        Invoice invoice = invoiceRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune facture pour la visite #" + visitId));
        return toDTO(invoice);
    }

    @Transactional
    public InvoiceDTO marquerPayee(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable, id=" + invoiceId));
        invoice.setStatut(InvoiceStatus.PAID);
        return toDTO(invoiceRepository.save(invoice));
    }

    @Transactional(readOnly = true)
    public byte[] genererFacturePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable, id=" + invoiceId));
        return pdfGeneratorService.genererFacture(invoice);
    }

    private InvoiceDTO toDTO(Invoice invoice) {
        List<InvoiceItemDTO> itemDTOs = invoice.getItems().stream()
                .map(i -> new InvoiceItemDTO(i.getId(), i.getLibelle(), i.getMontant()))
                .toList();
        return new InvoiceDTO(invoice.getId(), invoice.getVisit().getId(), invoice.getMontantTotal(),
                invoice.getStatut().name(), invoice.getDateEmission(), itemDTOs);
    }
}