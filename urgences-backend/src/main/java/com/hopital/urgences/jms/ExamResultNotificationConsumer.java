// jms/ExamResultNotificationConsumer.java
package com.hopital.urgences.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExamResultNotificationConsumer {

    @JmsListener(destination = ExamResultProducer.TOPIC_EXAM_RESULT, containerFactory = "topicListenerFactory")
    public void onResultAvailable(ExamResultEvent event) {
        log.info("🔔 [Notification médecin] Résultat disponible — examen {} ({}) — visite {}",
                event.examId(), event.type(), event.visitId());
    }
}