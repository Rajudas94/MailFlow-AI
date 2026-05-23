package com.mailflowai.ingestion.service;

import com.mailflowai.ingestion.dto.EmailEvent;
import com.mailflowai.ingestion.kafka.EmailEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final GmailService gmailService;
    private final EmailEventProducer emailEventProducer;

    @Scheduled(fixedDelay = 60000)
    public void pollEmails() {

        log.info("Polling Gmail for new emails...");
        List<EmailEvent> newEmails = gmailService.fetchNewEmails();

        if (newEmails.isEmpty()) // if the list is empty
        {
            log.info("No new emails to process");
            return;
        }

        log.info("Found {} new emails, sending to kafka", newEmails.size());

        for (EmailEvent event : newEmails) {
            emailEventProducer.sendEmailEvent(event);
            log.info("Sent to Kafka : {}", event.getSubject());
        }
    }
}









































































































