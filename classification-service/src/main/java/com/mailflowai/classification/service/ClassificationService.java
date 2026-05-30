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
    private final ClaudeApiService claudeApiService;

    public void classifyEmail(EmailEvent emailEvent)
    {
        log.info( "Classifying email : {} subject : {}", emailEvent.getEmailId(),  emailEvent.getSubject() );

        // check if already classified -> look up in database using repository interface
        if(classificationRepository.existsByEmailId(emailEvent.getEmailId()))
        {
            log.info("Email already classified : {}", emailEvent.getEmailId());
            return;
        }

        // Call Claude API
        String category = claudeApiService.classifyEmail(emailEvent.getSubject(), emailEvent.getBody());

        // if not classified , call Claude API
        // place "place-holders" for now
        Classification classification = new Classification();

        classification.setEmailId(emailEvent.getEmailId());
        classification.setCategory(category);
        classification.setConfidenceScore(0.95);
        classification.setReasoning("Classified by Ollama");
        classification.setClassifiedAt(LocalDateTime.now());
        classification.setModelUsed("llama3.2");

        classificationRepository.save(classification);

        log.info("Classified email {} as : {}", emailEvent.getEmailId(), category);

    }
}
