// this code will produce the response event to kafka after processing
package com.mailflowai.response.config;

import com.mailflowai.response.dto.ResponseEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.HashMap;

@Configuration
public class KafkaProducerConfig {

    @Value( "${spring.kafka.bootstrap-servers}" )
    private String bootstrapServers;

    @Bean
    public ProducerFactory <String, ResponseEvent> producerFactory() {

        // since we cannot send object to kafka , we have to convert object to json and send, we will use a dataStructure Map as it resembles to JSON
        Map <String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate <String, ResponseEvent> kafkaTemplate(){

        return new KafkaTemplate<>(producerFactory());
    }
}


