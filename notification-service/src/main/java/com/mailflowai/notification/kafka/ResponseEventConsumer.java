package com.mailflowai.notification.kafka;

import com.mailflowai.notification.dto.ResponseEvent;
import com.mailflowai.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseEventConsumer {

    private final NotificationService notificationService;

    // Consume email events from "response-events" topic using a consumer group. The group ID enables offset tracking, ensuring each email is processed exactly once and consumption resumes from the last committed offset after any interruption.
    @KafkaListener( topics = "${kafka.topic.response-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRoutingEvent(ResponseEvent responseEvent)
    {
        log.info("Received response email event from kafka for emailId : {} subject : {} ",
                responseEvent.getEmailId(),
                responseEvent.getSubject()
        );

        // call the processNotification function from notification service
        notificationService.processNotification(responseEvent);
    }
}




