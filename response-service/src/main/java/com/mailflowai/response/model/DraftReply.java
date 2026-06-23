package com.mailflowai.response.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "draft_replies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftReply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id")
    private UUID emailId;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
