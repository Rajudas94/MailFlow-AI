package com.mailflowai.routing.service;

import com.mailflowai.routing.dto.ClassificationEvent;
import com.mailflowai.routing.model.Queue;
import com.mailflowai.routing.model.Routing;
import com.mailflowai.routing.repository.QueueRepository;
import com.mailflowai.routing.repository.RoutingRepository;
import com.mailflowai.routing.kafka.RoutingEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class RoutingService {

    QueueRepository queueRepository;
    RoutingRepository routingRepository;
    RoutingEventProducer routingEventProducer;

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

        // 4. Write in routing Database
        Routing routing = new Routing();
        routing.setEmailId(classificationEvent.getEmailId());
        routing.setQueueId(queue != null ? queue.getId() : null);
        routing.setRoutedAt(LocalDateTime.now());
        routing.setPriority(priority);

        // Save the records permanently to Routing Database
        routingRepository.save(routing);
    }

    // Helper Functions
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
