package com.mailflowai.routing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "routings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Routing {

    @id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id", nullable = false)
    private UUID emailId;

    @Column(name = "queue_id")
    private UUID queueId;

    @Column(name = "routed_at")
    private LocalDateTime routedAt;

    @Column(name = "priority")
    private String priority;
}
