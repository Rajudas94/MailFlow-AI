package com.mailflowai.ingestion.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.mailflowai.ingestion.dto.EmailEvent;
import com.mailflowai.ingestion.model.Email;
import com.mailflowai.ingestion.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailService {

    private final Gmail gmailService;
    private final EmailRepository emailRepository;

    @Value("${gmail.user}")
    private String gmailUser;

    public List<EmailEvent> fetchNewEmails() {
        List<EmailEvent> emailEvents = new ArrayList<>();

        try {
            // Step 1 — Take list of unread mails from Gmail
            ListMessagesResponse response = gmailService.users()
                    .messages()
                    .list(gmailUser)
                    .setQ("is:unread")
                    .setMaxResults(10L)
                    .execute();

            if (response.getMessages() == null) {
                log.info("No new emails found");
                return emailEvents;
            }

            // Step 2 — for each message get full details
            for (Message message : response.getMessages()) {
                String gmailMessageId = message.getId();

                // Step 3 — If entry already added in PostgreSQL
                if (emailRepository.existsByGmailMessageId(gmailMessageId)) {
                    log.info("Email already processed: {}", gmailMessageId);
                    continue;
                }

                // Step 4 — fetch full message
                Message fullMessage = gmailService.users()
                        .messages()
                        .get(gmailUser, gmailMessageId)
                        .setFormat("full")
                        .execute();

                // Step 5 — extract fields
                String senderEmail = extractHeader(fullMessage, "From");
                String senderName = extractSenderName(senderEmail);
                String subject = extractHeader(fullMessage, "Subject");
                String body = extractBody(fullMessage);
                LocalDateTime receivedAt = extractReceivedAt(fullMessage);

                // Step 6 — save to PostgreSQL
                Email email = new Email();
                email.setGmailMessageId(gmailMessageId);
                email.setSenderEmail(senderEmail);
                email.setSenderName(senderName);
                email.setSubject(subject);
                email.setBody(body);
                email.setReceivedAt(receivedAt);
                email.setStatus("NEW");

                Email savedEmail = emailRepository.save(email);
                log.info("Saved email: {} from: {}", gmailMessageId, senderEmail);

                // Step 7 — convert to event for Kafka
                EmailEvent event = new EmailEvent(
                        savedEmail.getId(),
                        savedEmail.getGmailMessageId(),
                        savedEmail.getSenderEmail(),
                        savedEmail.getSenderName(),
                        savedEmail.getSubject(),
                        savedEmail.getBody(),
                        savedEmail.getStatus(),
                        savedEmail.getReceivedAt()

                );

                emailEvents.add(event);
            }

        } catch (Exception e) {
            log.error("Error fetching emails from Gmail: {}", e.getMessage());
        }

        return emailEvents;
    }

    // Helper methods

    private String extractHeader(Message message, String headerName) {
        if (message.getPayload() == null) return "";
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        if (headers == null) return "";
        return headers.stream()
                .filter(h -> h.getName().equalsIgnoreCase(headerName))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse("");
    }

    private String extractSenderName(String fromHeader) {
        // extract "John Doe" from " John Doe <john@example.com> "
        if (fromHeader.contains("<")) {
            return fromHeader.substring(0, fromHeader.indexOf("<")).trim();
        }
        return fromHeader;
    }

    private String extractBody(Message message) {
        if (message.getPayload() == null) return "";

        // Simple text body
        if (message.getPayload().getBody() != null
                && message.getPayload().getBody().getData() != null) {
            return decodeBase64(message.getPayload().getBody().getData());
        }

        // Multipart body — find text/plain part
        if (message.getPayload().getParts() != null) {
            for (MessagePart part : message.getPayload().getParts()) {
                if ("text/plain".equals(part.getMimeType())
                        && part.getBody() != null
                        && part.getBody().getData() != null) {
                    return decodeBase64(part.getBody().getData());
                }
            }
        }

        return "";
    }

    private String decodeBase64(String encoded) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
        return new String(decodedBytes);
    }

    private LocalDateTime extractReceivedAt(Message message) {
        if (message.getInternalDate() == null) return LocalDateTime.now();
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(message.getInternalDate()),
                ZoneId.systemDefault()
        );
    }
}