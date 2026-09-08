package com.hopital.urgences.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static com.hopital.urgences.config.JmsConfig.URGENT_CASE_TOPIC;

@Component
@RequiredArgsConstructor
public class UrgentCaseProducer {

    private final JmsTemplate jmsTemplate;

    public void publierUrgenceCritique(UrgentCaseEvent event) {
        jmsTemplate.convertAndSend(URGENT_CASE_TOPIC, event);
    }
}