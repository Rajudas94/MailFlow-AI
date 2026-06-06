package com.mailflowai.routing.kafka;

import com.mailflowai.routing.dto.ClassificationEvent;
import com.mailflowai.routing.service.RoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class ClassificationEventConsumer {

    private final RoutingService routingService;

    // Consume email events from "classified-emails" topic using a consumer group. The group ID enables offset tracking, ensuring each email is processed exactly once and consumption resumes from the last committed offset after any interruption.
    @KafkaListener( topics = "${kafka.topic.classified-emails}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClassificationEvent(ClassificationEvent classificationEvent)
    {
        log.info("Received classified email event from kafka for emailId : {} subject : {} ",
                classificationEvent.getEmailId(),
                classificationEvent.getSubject()
        );

        routingService.routeEmail(classificationEvent);
        // call the route email from the Routing Service
    }
}


