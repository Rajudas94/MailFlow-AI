package com.mailflowai.response.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutingEvent {

    private UUID emailId;
    private String gmailMessageId;
    private String senderEmail;
    private String senderName;
    private String subject;
    private String category;
    private String queueName;
    private String priority;
    private LocalDateTime routedAt;
}















