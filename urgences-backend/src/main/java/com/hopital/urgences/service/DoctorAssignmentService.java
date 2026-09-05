// service/DoctorAssignmentService.java
package com.hopital.urgences.service;

import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.DoctorRepository;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorAssignmentService {

    private final DoctorRepository doctorRepository;
    private final EmergencyVisitRepository visitRepository;

    @Transactional
    public void assignAvailableDoctor(Long visitId) {
        EmergencyVisit visite = visitRepository.findById(visitId).orElseThrow();
        doctorRepository.findFirstByStatut(DoctorStatus.DISPONIBLE).ifPresentOrElse(doctor -> {
            doctor.setStatut(DoctorStatus.OCCUPE);
            visite.setMedecin(doctor);
            visite.setStatut(VisitStatus.EN_CONSULTATION);
            doctorRepository.save(doctor);
            visitRepository.save(visite);
        }, () -> {
            // Aucun médecin disponible : à gérer (escalade, notification renforcée...)
        });
    }
}