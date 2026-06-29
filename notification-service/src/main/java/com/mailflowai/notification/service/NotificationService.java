package com.mailflowai.notification.service;

import com.mailflowai.notification.dto.ResponseEvent;
import com.mailflowai.notification.model.AuditLog;
import com.mailflowai.notification.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AuditLogRepository auditLogRepository;

    public void processNotification(ResponseEvent responseEvent)
    {
        // Log for receiving the mail
        log.info( "Received email : {} for processing Notification", responseEvent.getEmailId());

        // Check if ollama has generated response before
        if(auditLogRepository.existsByEmailId(responseEvent.getEmailId()))
        {
            log.info( "Notification already processes for emailId {}", responseEvent.getEmailId() );
            return;
        }

        // Write to Database

        // 1. Create DB Object
        AuditLog entry = new AuditLog();

        // 2. Set the fields
       entry.setEmailId(responseEvent.getEmailId());
       entry.setAction("NOTIFICATION_SENT");
       entry.setPerformedBy("notification-service");
       entry.setDetails("Email from " + responseEvent.getSenderName()
                + " | Subject: " + responseEvent.getSubject()
                + " | Category: " + responseEvent.getCategory()
                + " | Draft Status: " + responseEvent.getApprovalStatus()
       );
       entry.setCreatedAt(LocalDateTime.now());

        // 3. Save the entries permanently
        auditLogRepository.save(entry);

        // 4. Log to console
        log.info("Audit Log saved for emailId : {} action : NOTIFICATION-SENT", responseEvent.getEmailId());

        log.info("=== NOTIFICATION ===");
        log.info("From : {} <{}>", responseEvent.getSenderName(), responseEvent.getSenderEmail());
        log.info("Subject : {}", responseEvent.getSubject());
        log.info("Category : {}", responseEvent.getCategory());
        log.info("Draft Reply : {}", responseEvent.getDraftReply());
        log.info("Status : {}", responseEvent.getApprovalStatus());
        log.info("====================");
    }
}

