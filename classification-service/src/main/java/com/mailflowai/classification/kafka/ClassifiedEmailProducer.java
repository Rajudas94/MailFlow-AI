package com.mailflowai.classification.kafka;

import com.mailflowai.classification.dto.ClassificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j

public class ClassifiedEmailProducer {

    private final KafkaTemplate <String, ClassificationEvent> kafkaTemplate;

    // get topic, where the classified email will get published
    @Value( "${kafka.topic.classified-emails}" )
    private String classifiedEmailsTopic;

    public void sendClassificationEmail(ClassificationEvent event)
    {
        // show in log that email is send to the designation topic
        log.info("Sending Classification event to kafka topic : {} for emailId : {}",
                classifiedEmailsTopic, event.getEmailId());

        // send to kafka (string, int, value)
        kafkaTemplate.send(classifiedEmailsTopic, event.getEmailId().toString(), event );

        // show output in log
        log.info( "Classification event sent successfully for EmailId : {}", event.getEmailId() );
    }
}
