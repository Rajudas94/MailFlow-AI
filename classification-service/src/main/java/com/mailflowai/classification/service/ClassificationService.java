package com.mailflowai.classification.service;

import com.mailflowai.classification.dto.EmailEvent;
import com.mailflowai.classification.model.Classification;
import com.mailflowai.classification.repository.ClassificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassificationService {

    private final ClassificationRepository classificationRepository;

    public void classifyEmail(EmailEvent emailEvent)
    {
        log.info( "Classifying email : {} subject : {}", emailEvent.getEmailId(),  emailEvent.getSubject() );

        // if already classified
        if(classificationRepository.existsByEmail(emailEvent.getEmailId()))
        {
            log.info("Email already classified : {}", emailEvent.getEmailId());
            return;
        }

        // if not classified , call Claude API
        // place place-holders for now
        Classification classification = new Classification();

        classification.setEmailId(emailEvent.getEmailId());
        classification.setCategory("Pending");
        classification.setConfidenceScore(0.0);
        classification.setReasoning("Awaiting Claude API Integration");
        classification.setClassifiedAt(LocalDateTime.now());
        classification.setModelUsed("None");

        classificationRepository.save(classification);

        log.info("Saved placeholder classification for emailId : {}", emailEvent.getEmailId());

    }
}
