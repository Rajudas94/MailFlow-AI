package com.mailflowai.response.service;

import com.mailflowai.response.dto.ResponseEvent;
import com.mailflowai.response.kafka.ResponseEventProducer;
import com.mailflowai.response.model.DraftReply;
import com.mailflowai.response.repository.DraftReplyRepository;
import com.mailflowai.response.dto.RoutingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseService {

    private final ResponseEventProducer responseEventProducer;
    private final OllamaService ollamaService;
    private final DraftReplyRepository draftReplyRepository;

    public void generateResponse(RoutingEvent routingEvent)
    {
        // Log for receiving the mail
        log.info( "Received email : {} for drafting response", routingEvent.getEmailId() );

        // Check if ollama has generated response before
        if(draftReplyRepository.existsByEmailId(routingEvent.getEmailId()))
        {
            log.info( "Draft already generated for emailId {}", routingEvent.getEmailId() );
            return;
        }

        // Generate the draft
        String generatedDraft = ollamaService.generateDraftReply(null,
                routingEvent.getSubject(),
                routingEvent.getCategory(),
                routingEvent.getSenderName()
        );

        // Write to Database
        // 1. Create DB Object and set EmailId, Body(generate0d above), Approval status to "Pending"(since no human has verified the response yet) and generatedAt
        DraftReply draftReply = new DraftReply();
        draftReply.setEmailId(routingEvent.getEmailId());
        draftReply.setBody(generatedDraft);
        draftReply.setApprovalStatus("PENDING");
        draftReply.setGeneratedAt(LocalDateTime.now());

        // 2. Save the entries permanently
        draftReplyRepository.save(draftReply);

        // Produce to Kafka
        // 1. Create a response event object and set the fields
        ResponseEvent responseEvent = new ResponseEvent();

        responseEvent.setEmailId(routingEvent.getEmailId());
        responseEvent.setSenderName(routingEvent.getSenderName());
        responseEvent.setSenderEmail(routingEvent.getSenderEmail());
        responseEvent.setSubject(routingEvent.getSubject());
        responseEvent.setCategory(routingEvent.getCategory());
        responseEvent.setDraftReply(generatedDraft);
        responseEvent.setApprovalStatus("PENDING");
        responseEvent.setGeneratedAt(LocalDateTime.now());

        // 2. Call function for producing response event to kafka
        responseEventProducer.sendResponseEvent(responseEvent);

        // Log to console
        log.info("Response Event sent successfully to kafka for email : {}", routingEvent.getEmailId());
    }

}
