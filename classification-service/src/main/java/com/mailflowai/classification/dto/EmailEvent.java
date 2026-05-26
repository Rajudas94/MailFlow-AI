package com.mailflowai.classification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {

    private String senderName;
    private String senderEmail;
    private String subject;
    private LocalDateTime receivedAt;
    private String body;
    private UUID emailId;
    private String gmailMessageId;
    private String status;
}
