package com.mailflowai.ingestion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table (name = " emails ")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "gmail_message_id", unique = true)
    private String gmailMessageId;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "status")
    private String status;
}

/*

What Each Annotation Does
@Entity — tells Spring this class maps to a database table
@Table(name = "emails") — tells Spring the table name is emails
@Id — this field is the primary key
@GeneratedValue(strategy = GenerationType.UUID) — PostgreSQL auto-generates a UUID, you never set it manually
@Column — maps the Java field to a specific database column
@Data — Lombok magic. Automatically generates getters, setters, toString, equals. Without Lombok you'd write 50 extra lines manually.
@NoArgsConstructor and @AllArgsConstructor — Lombok generates two constructors automatically

*/
