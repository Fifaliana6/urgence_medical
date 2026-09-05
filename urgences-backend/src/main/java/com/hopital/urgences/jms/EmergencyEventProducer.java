package com.hopital.urgences.jms;

import com.hopital.urgences.model.EmergencyVisit;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmergencyEventProducer {

    public static final String TOPIC_URGENT_CASE = "urgent.case.created";

    private final JmsTemplate topicJmsTemplate;

    public void publishUrgentCase(EmergencyVisit visite) {
        UrgentCaseEvent event = new UrgentCaseEvent(
                visite.getId(),
                visite.getPatient().getId(),
                visite.getSymptomes(),
                visite.getNiveauUrgence(),
                visite.getDateArrivee()
        );
        topicJmsTemplate.convertAndSend(TOPIC_URGENT_CASE, event);
    }
}