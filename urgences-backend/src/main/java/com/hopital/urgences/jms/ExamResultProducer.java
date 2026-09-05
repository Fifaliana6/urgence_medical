// jms/ExamResultProducer.java
package com.hopital.urgences.jms;

import com.hopital.urgences.model.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamResultProducer {

    public static final String TOPIC_EXAM_RESULT = "exam.result.available";

    private final JmsTemplate topicJmsTemplate;

    public void publishResultAvailable(Exam exam) {
        ExamResultEvent event = new ExamResultEvent(
                exam.getId(),
                exam.getConsultation().getId(),
                exam.getConsultation().getVisite().getId(),
                exam.getType(),
                exam.getResultat()
        );
        topicJmsTemplate.convertAndSend(TOPIC_EXAM_RESULT, event);
    }
}