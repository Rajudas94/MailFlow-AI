// getting published event from kafka

package com.mailflowai.routing.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationEvent {

    private UUID emailId;
    private String category;
    private LocalDateTime classifiedAt;
    private String SenderName;
    private String SenderEmail;
    private String subject;
    private String GmailMessageId;
}

