package com.mailflowai.ingestion.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmailEvent {

    private UUID emailId;
    private String senderName;
    private String senderEmail;
    private String subject;
    private String body;
    private String gmailMessageId;
    private String status;
    private LocalDateTime receivedAt;
}
