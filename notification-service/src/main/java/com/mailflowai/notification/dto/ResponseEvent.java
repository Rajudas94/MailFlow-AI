package com.mailflowai.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEvent {

    private UUID emailId;
    private String senderName;
    private String senderEmail;
    private String subject;
    private String category;
    private String draftReply;
    private String approvalStatus;
    private LocalDateTime generatedAt;
}

