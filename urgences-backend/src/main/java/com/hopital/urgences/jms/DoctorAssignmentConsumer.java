// jms/DoctorAssignmentConsumer.java
package com.hopital.urgences.jms;

import com.hopital.urgences.service.DoctorAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorAssignmentConsumer {

    private final DoctorAssignmentService doctorAssignmentService;

    @JmsListener(destination = EmergencyEventProducer.TOPIC_URGENT_CASE, containerFactory = "topicListenerFactory")
    public void onUrgentCase(UrgentCaseEvent event) {
        log.info("[Consumer Médecin] Urgence {} niveau {} -> recherche d'un médecin", event.visitId(), event.niveauUrgence());
        doctorAssignmentService.assignAvailableDoctor(event.visitId());
    }
}