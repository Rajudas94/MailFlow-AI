package com.mailflowai.classification.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "classifications" )
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email_id", nullable = false)
    private UUID emailId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "reasoning", columnDefinition = "TEXT")
    private String reasoning;

    @Column( name = "confidence_score" )
    private Double confidenceScore;

    @Column( name = "classified_at" )
    private LocalDateTime classifiedAt;

    @Column( name = "model_used" )
    private String modelUsed;
}
