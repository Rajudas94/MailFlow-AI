// This file actually publishes the response event to - "response-events" topic
package com.mailflowai.response.kafka;

import com.mailflowai.response.dto.ResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseEventProducer {

    private final KafkaTemplate <String, ResponseEvent> kafkaTemplate;

    // get topic, where the response event will be published
    @Value( "${kafka.topic.response-events}" )
    private String responseEmailsTopic;

    public void sendResponseEvent(ResponseEvent event)
    {
        // show in log that email is send to the designation topic
        log.info("Sending Response event to kafka topic : {} for emailId : {}",
                responseEmailsTopic, event.getEmailId());

        // send to kafka (topic name, EmailId, event)
        kafkaTemplate.send(responseEmailsTopic, event.getEmailId().toString(), event);

        // show output in log
        log.info( "Response event sent successfully for EmailId : {}", event.getEmailId() );
    }
}


