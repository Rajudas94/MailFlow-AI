package com.mailflowai.classification.kafka;

import com.mailflowai.classification.dto.EmailEvent;
import com.mailflowai.classification.service.ClassificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class EmailEventConsumer {

    private final ClassificationService classificationService;

    // Consume email events from "email-events" topic using a consumer group. The group ID enables offset tracking, ensuring each email is processed exactly once and consumption resumes from the last committed offset after any interruption.
    @KafkaListener( topics = "${kafka.topic.email-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEmailEvent(EmailEvent emailEvent)
    {
        log.info("Received email event from kafka for emailId : {} subject : {} ",
                emailEvent.getEmailId(),
                emailEvent.getSubject()
        );
        classificationService.classifyEmail(emailEvent);
        // call the classify email from the Classification Service
    }
}




































