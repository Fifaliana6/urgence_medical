package com.hopital.urgences.service;

import com.hopital.urgences.model.Patient;
import com.hopital.urgences.model.exam.Exam;
import com.hopital.urgences.model.invoice.Invoice;
import com.hopital.urgences.model.invoice.InvoiceItem;
import com.hopital.urgences.model.invoice.InvoiceStatus;
import com.hopital.urgences.model.visite.EmergencyVisit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final float MARGE = 50f;
    private static final float LARGEUR_PAGE = PDRectangle.A4.getWidth();
    private static final float HAUTEUR_PAGE = PDRectangle.A4.getHeight();

    private String formaterMontant(double montant) {
        return String.format(Locale.US, "%,.0f", montant);
    }

    public byte[] genererFacture(Invoice invoice) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont police = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont policeGrasse = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            EmergencyVisit visit = invoice.getVisit();
            Patient patient = visit.getPatient();

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = HAUTEUR_PAGE - MARGE;

                y = ecrireTexte(cs, policeGrasse, 16, "FACTURE HOSPITALIÈRE", MARGE, y);
                y -= 6;
                y = ecrireTexte(cs, police, 10, "Plateforme de gestion des urgences médicales", MARGE, y);
                y -= 20;

                y = ecrireTexte(cs, policeGrasse, 11, "Facture n° " + invoice.getId(), MARGE, y);
                y = ecrireTexte(cs, police, 11, "Émise le " + invoice.getDateEmission().format(DATE_FORMAT), MARGE, y);
                y = ecrireTexte(cs, police, 11, "Statut : " + (invoice.getStatut() == InvoiceStatus.PAID ? "Payée" : "Non payée"), MARGE, y);
                y -= 15;

                y = ecrireTexte(cs, policeGrasse, 11, "Patient", MARGE, y);
                y = ecrireTexte(cs, police, 11, patient.getPrenom() + " " + patient.getNom(), MARGE, y);
                if (patient.getDateNaissance() != null) {
                    y = ecrireTexte(cs, police, 11, "Né(e) le " + patient.getDateNaissance(), MARGE, y);
                }
                y = ecrireTexte(cs, police, 11, "Visite n° " + visit.getId() + " — arrivée le " + visit.getHeureArrivee().format(DATE_FORMAT), MARGE, y);
                y -= 20;

                y = ecrireTexte(cs, policeGrasse, 12, "Détail des prestations", MARGE, y);
                y -= 8;

                float xMontant = LARGEUR_PAGE - MARGE - 90;

                ecrireTexte(cs, policeGrasse, 10, "Libellé", MARGE, y);
                ecrireTexte(cs, policeGrasse, 10, "Montant (Ar)", xMontant, y);
                y -= 6;
                tracerLigne(cs, MARGE, y, LARGEUR_PAGE - MARGE, y);
                y -= 16;

                for (InvoiceItem item : invoice.getItems()) {
                    ecrireTexte(cs, police, 10, item.getLibelle(), MARGE, y);
                    ecrireTexte(cs, police, 10, formaterMontant(item.getMontant()), xMontant, y);
                    y -= 18;
                }

                y -= 4;
                tracerLigne(cs, MARGE, y, LARGEUR_PAGE - MARGE, y);
                y -= 22;

                ecrireTexte(cs, policeGrasse, 13, "Total : " + formaterMontant(invoice.getMontantTotal()) + " Ar", MARGE, y);
            }

            return toByteArray(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur lors de la génération du PDF de facture", e);
        }
    }

    public byte[] genererRapportExamen(Exam exam) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont police = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont policeGrasse = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            EmergencyVisit visit = exam.getConsultation().getVisit();
            Patient patient = visit.getPatient();

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = HAUTEUR_PAGE - MARGE;

                y = ecrireTexte(cs, policeGrasse, 16, "RAPPORT D'EXAMEN", MARGE, y);
                y -= 6;
                y = ecrireTexte(cs, police, 10, "Plateforme de gestion des urgences médicales", MARGE, y);
                y -= 20;

                y = ecrireTexte(cs, policeGrasse, 11, "Patient", MARGE, y);
                y = ecrireTexte(cs, police, 11, patient.getPrenom() + " " + patient.getNom(), MARGE, y);
                y = ecrireTexte(cs, police, 11, "Visite n° " + visit.getId(), MARGE, y);
                y -= 15;

                y = ecrireTexte(cs, policeGrasse, 11, "Examen", MARGE, y);
                y = ecrireTexte(cs, police, 11, "Type : " + exam.getType(), MARGE, y);
                y = ecrireTexte(cs, police, 11, "Libellé : " + exam.getLibelle(), MARGE, y);
                y = ecrireTexte(cs, police, 11, "Demandé par : " + exam.getDemandePar().getNom()
                        + " le " + exam.getDateDemande().format(DATE_FORMAT), MARGE, y);
                if (exam.getRealisePar() != null) {
                    y = ecrireTexte(cs, police, 11, "Réalisé par : " + exam.getRealisePar().getNom(), MARGE, y);
                }
                if (exam.getDateResultat() != null) {
                    y = ecrireTexte(cs, police, 11, "Résultat saisi le : " + exam.getDateResultat().format(DATE_FORMAT), MARGE, y);
                }
                y -= 15;

                y = ecrireTexte(cs, policeGrasse, 12, "Résultat", MARGE, y);
                y -= 8;

                String resultat = exam.getResultat() != null ? exam.getResultat() : "Résultat non disponible.";
                for (String ligne : decouperTexte(resultat, police, 11, LARGEUR_PAGE - 2 * MARGE)) {
                    y = ecrireTexte(cs, police, 11, ligne, MARGE, y);
                }
            }

            return toByteArray(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur lors de la génération du PDF de rapport", e);
        }
    }

    private float ecrireTexte(PDPageContentStream cs, PDFont police, float taille, String texte, float x, float y) throws IOException {
        cs.setFont(police, taille);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(texte);
        cs.endText();
        return y - (taille + 5);
    }

    private void tracerLigne(PDPageContentStream cs, float x1, float y1, float x2, float y2) throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private List<String> decouperTexte(String texte, PDFont police, float taille, float largeurMax) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (String paragraphe : texte.split("\n")) {
            StringBuilder ligneCourante = new StringBuilder();
            for (String mot : paragraphe.split(" ")) {
                String essai = ligneCourante.isEmpty() ? mot : ligneCourante + " " + mot;
                if (police.getStringWidth(essai) / 1000 * taille > largeurMax && !ligneCourante.isEmpty()) {
                    lignes.add(ligneCourante.toString());
                    ligneCourante = new StringBuilder(mot);
                } else {
                    ligneCourante = new StringBuilder(essai);
                }
            }
            lignes.add(ligneCourante.toString());
        }
        return lignes;
    }

    private byte[] toByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        return out.toByteArray();
    }
}