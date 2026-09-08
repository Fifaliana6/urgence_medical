package com.hopital.urgences.messaging;

import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.model.visite.VisitStatus;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.repository.UserRepository;
import com.hopital.urgences.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hopital.urgences.config.JmsConfig.URGENT_CASE_TOPIC;

@Component
@RequiredArgsConstructor
public class MedecinAssignmentConsumer {

    private static final List<VisitStatus> STATUTS_ACTIFS = List.of(
            VisitStatus.EN_ATTENTE, VisitStatus.EN_CONSULTATION, VisitStatus.EN_EXAMEN, VisitStatus.HOSPITALIZED);

    private final UserRepository userRepository;
    private final EmergencyVisitRepository visitRepository;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    @JmsListener(destination = URGENT_CASE_TOPIC, containerFactory = "topicListenerFactory")
    public void assignerMedecin(UrgentCaseEvent event) {
        List<User> medecinsDisponibles = userRepository.findByRoleAndStatutAndDisponibleTrue(Role.MEDECIN, AccountStatus.APPROVED);

        if (medecinsDisponibles.isEmpty()) {
            notificationPublisher.notifierRole(Role.ADMIN,
                    "Aucun médecin disponible pour l'urgence critique #" + event.getVisitId(), event.getVisitId());
            return;
        }
        User medecinChoisi = medecinsDisponibles.get(0);
        long chargeMin = visitRepository.countByMedecinIdAndStatusIn(medecinChoisi.getId(), STATUTS_ACTIFS);

        for (User candidat : medecinsDisponibles) {
            long charge = visitRepository.countByMedecinIdAndStatusIn(candidat.getId(), STATUTS_ACTIFS);
            if (charge < chargeMin) {
                chargeMin = charge;
                medecinChoisi = candidat;
            }
        }

        EmergencyVisit visit = visitRepository.findById(event.getVisitId()).orElse(null);
        if (visit == null) return;

        visit.setMedecin(medecinChoisi);
        visitRepository.save(visit);

        notificationPublisher.notifierUtilisateur(medecinChoisi.getId(),
                "Urgence CRITIQUE assignée : " + event.getPatientNomComplet() + " — " + event.getSymptomes(),
                event.getVisitId());
    }
}