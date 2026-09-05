// jms/RoomReservationConsumer.java
package com.hopital.urgences.jms;

import com.hopital.urgences.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomReservationConsumer {

    private final RoomService roomService;

    @JmsListener(destination = EmergencyEventProducer.TOPIC_URGENT_CASE, containerFactory = "topicListenerFactory")
    public void onUrgentCase(UrgentCaseEvent event) {
        log.info("[Consumer Salle] Réservation d'une salle de déchocage pour la visite {}", event.visitId());
        roomService.reserveDechocageRoom(event.visitId());
    }
}