package com.hopital.urgences.controller;

import com.hopital.urgences.dto.exam.ExamDTO;
import com.hopital.urgences.dto.exam.ExamResultRequest;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/attente")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','LABO_IMAGERIE')")
    public List<ExamDTO> enAttente() {
        return examService.examsEnAttente();
    }

    @PutMapping("/{id}/resultat")
    @PreAuthorize("hasRole('LABO_IMAGERIE')")
    public ExamDTO saisirResultat(@PathVariable Long id, @Valid @RequestBody ExamResultRequest request,
                                   @AuthenticationPrincipal CustomUserDetails labo) {
        return examService.saisirResultat(id, request, labo);
    }

    @GetMapping(value = "/{id}/rapport", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','LABO_IMAGERIE')")
    public ResponseEntity<byte[]> telechargerRapport(@PathVariable Long id) {
        byte[] pdf = examService.genererRapportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-examen-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}