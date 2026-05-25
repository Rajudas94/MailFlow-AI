package com.mailflowai.ingestion.controller;

import com.mailflowai.ingestion.service.IngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/ingestion" )
@RequiredArgsConstructor
@Slf4j
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping( "/fetch" )
    public ResponseEntity <String> fetchEmails()
    {
        log.info("Manual Email fetch via API");
        ingestionService.pollEmails();
        return ResponseEntity.ok("Email fetch triggered successfully");
    }

    @GetMapping("/health")
    public ResponseEntity <String> health()
    {
        return ResponseEntity.ok("Ingestion service is running");
    }
}
