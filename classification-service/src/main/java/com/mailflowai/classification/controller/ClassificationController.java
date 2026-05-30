package com.mailflowai.classification.controller;

import com.mailflowai.classification.dto.EmailEvent;
import com.mailflowai.classification.service.ClassificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classification")
@RequiredArgsConstructor
@Slf4j
public class ClassificationController {

    private final ClassificationService classificationService;

    @PostMapping("/test")
    public ResponseEntity < String > testClassification( @RequestBody EmailEvent emailEvent)
    {
        log.info("Manual classification test triggered for subject : {}", emailEvent.getSubject());
        classificationService.classifyEmail(emailEvent);

        return ResponseEntity.ok("Classification triggered for : " + emailEvent.getSubject() );
    }

    // for verifying if the Service is working or not
    @GetMapping("/health")
    public ResponseEntity < String > health()
    {
        return ResponseEntity.ok("Classification service is running");
    }
}















































