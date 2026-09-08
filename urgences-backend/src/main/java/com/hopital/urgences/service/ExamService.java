package com.hopital.urgences.service;

import com.hopital.urgences.dto.exam.ExamDTO;
import com.hopital.urgences.dto.exam.ExamResultRequest;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.exam.Exam;
import com.hopital.urgences.model.exam.ExamStatus;
import com.hopital.urgences.repository.ExamRepository;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuditService auditService;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional(readOnly = true)
    public List<ExamDTO> examsEnAttente() {
        return examRepository.findByStatut(ExamStatus.EN_ATTENTE).stream().map(this::toDTO).toList();
    }

    @Transactional
    public ExamDTO saisirResultat(Long examId, ExamResultRequest request, CustomUserDetails laboAuth) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen introuvable, id=" + examId));

        exam.setResultat(request.getResultat());
        exam.setStatut(ExamStatus.RESULTATS_DISPONIBLES);
        exam.setRealisePar(laboAuth.getUser());
        exam.setDateResultat(LocalDateTime.now());

        exam = examRepository.save(exam);

        Long medecinId = exam.getConsultation().getMedecin().getId();
        Long visitId = exam.getConsultation().getVisit().getId();

        notificationPublisher.notifierUtilisateur(medecinId,
                "Résultat disponible pour l'examen \"" + exam.getLibelle() + "\" — visite #" + visitId,
                visitId);

        auditService.enregistrer(laboAuth.getUser(), "SAISIE_RESULTAT_EXAMEN", visitId,
                exam.getLibelle() + " — résultat saisi");

        return toDTO(exam);
    }

    @Transactional(readOnly = true)
    public byte[] genererRapportPdf(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen introuvable, id=" + examId));
        return pdfGeneratorService.genererRapportExamen(exam);
    }

    private ExamDTO toDTO(Exam e) {
        var visit = e.getConsultation().getVisit();
        return new ExamDTO(e.getId(), visit.getId(), e.getType(), e.getLibelle(), e.getStatut(),
                e.getDemandePar().getNom(), e.getDateDemande(),
                e.getRealisePar() != null ? e.getRealisePar().getNom() : null,
                e.getResultat(), e.getDateResultat(),
                visit.getPatient().getPrenom() + " " + visit.getPatient().getNom());
    }
}