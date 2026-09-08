package com.hopital.urgences.messaging;

import com.hopital.urgences.model.salle.Salle;
import com.hopital.urgences.model.salle.TypeSalle;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.visite.EmergencyVisit;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.service.ResourceService;
import com.hopital.urgences.service.VisitService;
import com.hopital.urgences.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.hopital.urgences.config.JmsConfig.URGENT_CASE_TOPIC;

@Component
@RequiredArgsConstructor
public class SalleConsumer {

    private final EmergencyVisitRepository visitRepository;
    private final ResourceService resourceService;
    private final VisitService visitService;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    @JmsListener(destination = URGENT_CASE_TOPIC, containerFactory = "topicListenerFactory")
    public void reserverSalleDechocage(UrgentCaseEvent event) {
        EmergencyVisit visit = visitRepository.findById(event.getVisitId()).orElse(null);
        if (visit == null) return;

        Salle salle = resourceService.trouverSalleDisponible(TypeSalle.DECHOCAGE);

        if (salle == null) {
            notificationPublisher.notifierRole(Role.ADMIN,
                    "Aucune salle de déchocage disponible pour la visite #" + event.getVisitId(), event.getVisitId());
            return;
        }

        resourceService.changerDisponibiliteSalle(salle.getId(), false);
        visitService.assignerSalle(visit, salle);

        notificationPublisher.notifierRole(Role.ADMIN,
                "Salle \"" + salle.getNom() + "\" réservée pour la visite #" + event.getVisitId(), event.getVisitId());
    }
}