package com.mailflowai.response.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
