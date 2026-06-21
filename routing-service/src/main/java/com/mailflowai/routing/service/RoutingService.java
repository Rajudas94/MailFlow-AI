package com.mailflowai.routing.service;

import com.mailflowai.routing.dto.ClassificationEvent;
import com.mailflowai.routing.dto.RoutingEvent;
import com.mailflowai.routing.model.Queue;
import com.mailflowai.routing.model.Routing;
import com.mailflowai.routing.repository.QueueRepository;
import com.mailflowai.routing.repository.RoutingRepository;
import com.mailflowai.routing.kafka.RoutingEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutingService {

    private final QueueRepository queueRepository;
    private final RoutingRepository routingRepository;
    private final RoutingEventProducer routingEventProducer;

    public void routeEmail(ClassificationEvent classificationEvent)
    {
        // first log that email is being routed to category
        log.info( " Routing Email : {} category : {} ", classificationEvent.getEmailId(), classificationEvent.getCategory() );

        // Route the email event

        // 1. Check if already routed , see in RoutingRepository
        if(routingRepository.existsByEmailId(classificationEvent.getEmailId()))
        {
            log.info( "Email already routed : {} ", classificationEvent.getEmailId() );
            return;
        }

        // 2. if it doesn't exist, route the email to the specific queue
        String priority = getPriority(classificationEvent.getCategory());

        // 3. Find the right Queue
        Optional < Queue > queueOptional =
                queueRepository.findByCategory(classificationEvent.getCategory());

        // Base case for point 3
        if(queueOptional.isEmpty())
        {
            log.warn( "No queue found for category : {}, using default queue...", classificationEvent.getCategory()  );
            return;
        }

        Queue queue = queueOptional.orElse(null); // ???

        // 4. Write in routing Database (temporary log entry)
        log.info("Writing data to Routing DB fields for email {}", classificationEvent.getEmailId());
        Routing routing = new Routing();
        routing.setEmailId(classificationEvent.getEmailId());
        routing.setQueueId(queue != null ? queue.getId() : null);
        routing.setRoutedAt(LocalDateTime.now());
        routing.setPriority(priority);

        // Save the records permanently to Routing Database
        routingRepository.save(routing);

        // for publishing the routing event back to kafka
        RoutingEvent routingEvent = new RoutingEvent();

        routingEvent.setEmailId(classificationEvent.getEmailId());
        routingEvent.setGmailMessageId(classificationEvent.getGmailMessageId());
        routingEvent.setSenderEmail(classificationEvent.getSenderEmail());
        routingEvent.setSenderName(classificationEvent.getSenderName());
        routingEvent.setSubject(classificationEvent.getSubject());
        routingEvent.setCategory(classificationEvent.getCategory());
        routingEvent.setQueueName(queue != null ? queue.getName() : "General Queue");
        routingEvent.setPriority(priority);
        routingEvent.setRoutedAt(LocalDateTime.now());

        routingEventProducer.sendRoutingEvent(routingEvent);
    }

    // Helper Function
    String getPriority(String category)
    {
        return switch (category) {

            case "COMPLAINT" -> "HIGH";
            case "LEAD" -> "MEDIUM";
            case "SUPPORT"-> "MEDIUM";
            case "SPAM"-> "LOW";
            default -> "LOW";
        };
    }
}
