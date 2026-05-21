package com.mailflowai.ingestion.kafka;

import com.mailflowai.ingestion.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class EmailEventProducer {

    private final KafkaTemplate <String, EmailEvent> kafkaTemplate;

    @Value( "${kafka.topic.email-events}" )
    private String emailEventsTopic;

    public void sendEmailEvent(EmailEvent emailEvent)
    {
        log.info("Sending email event to Kafka topic : {} for emailId : {}",
                emailEventsTopic, emailEvent.getEmailId() );

        kafkaTemplate.send(emailEventsTopic,
                emailEvent.getEmailId().toString(),
                emailEvent);

        log.info("Email event sent successfully for emailId : {}",
                emailEvent.getEmailId());
    }
}
