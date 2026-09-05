// jms/NotificationConsumer.java
package com.hopital.urgences.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @JmsListener(destination = EmergencyEventProducer.TOPIC_URGENT_CASE, containerFactory = "topicListenerFactory")
    public void onUrgentCase(UrgentCaseEvent event) {
        // Ici : notification interne (websocket, email, SMS...). On log pour l'instant.
        log.warn("🚨 [Notification équipe] Cas CRITIQUE — visite {} — symptômes: {}", event.visitId(), event.symptomes());
    }
}