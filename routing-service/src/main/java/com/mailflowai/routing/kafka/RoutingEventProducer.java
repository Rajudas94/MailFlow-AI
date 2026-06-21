// This file actually publishes the routing event to -
// - "routed-emails" topic

package com.mailflowai.routing.kafka;

import com.mailflowai.routing.dto.RoutingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class RoutingEventProducer {

    private final KafkaTemplate <String, RoutingEvent> kafkaTemplate;

    // get topic, where the routing event will get published
    @Value( "${kafka.topic.routing-events}" )
    private String routingEmailsTopic;

    public void sendRoutingEvent(RoutingEvent event)
    {
        // show in log that email is send to the designation topic
        log.info("Sending Routing event to kafka topic : {} for emailId : {}",
                routingEmailsTopic, event.getEmailId());

        // send to kafka (topic name, EmailId, event)
        kafkaTemplate.send(routingEmailsTopic, event.getEmailId().toString(), event);

        // show output in log
        log.info( "Routing event sent successfully for EmailId : {}", event.getEmailId() );
    }
}


