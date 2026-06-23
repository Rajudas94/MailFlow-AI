package com.mailflowai.response.kafka;

import com.mailflowai.response.dto.RoutingEvent;
// import com.mailflowai.response.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutingEventConsumer {

    // private final RoutingService routingService;

    // Consume email events from "routing-events" topic using a consumer group. The group ID enables offset tracking, ensuring each email is processed exactly once and consumption resumes from the last committed offset after any interruption.
    @KafkaListener( topics = "${kafka.topic.routing-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRoutingEvent(RoutingEvent routingEvent)
    {
        log.info("Received routed email event from kafka for emailId : {} subject : {} ",
                routingEvent.getEmailId(),
                routingEvent.getSubject()
        );

        // routingService.draftResponse(routingEvent);
        // call the route email from the Routing Service
    }
}



