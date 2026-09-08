package com.hopital.urgences.messaging;

import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.hopital.urgences.config.JmsConfig.URGENT_CASE_TOPIC;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationPublisher notificationPublisher;

    @JmsListener(destination = URGENT_CASE_TOPIC, containerFactory = "topicListenerFactory")
    public void alerterEquipe(UrgentCaseEvent event) {
        String message = "URGENCE CRITIQUE : " + event.getPatientNomComplet()
                + " (" + event.getSymptomes() + ") — arrivée à traiter immédiatement.";

        notificationPublisher.notifierRole(Role.ADMIN, message, event.getVisitId());
        notificationPublisher.notifierRole(Role.RECEPTIONIST, message, event.getVisitId());
    }
}