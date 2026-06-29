package com.mailflowai.notification.controller;

import com.mailflowai.notification.model.AuditLog;
import com.mailflowai.notification.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    // private final ResponseService responseService;
    private final AuditLogRepository auditLogRepository;

    // for fetching the list of all audit logs
    @GetMapping("/logs")
    public ResponseEntity < List<AuditLog> > getAllAuditLogs()
    {
        List < AuditLog > logs = auditLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    // for fetching audit logs of a specific emailId
    @GetMapping("/logs/{emailId}")
    public ResponseEntity < List<AuditLog> > specificLog(@PathVariable UUID emailId) {

        List < AuditLog > logs = auditLogRepository.findByEmailId(emailId);
        return ResponseEntity.ok(logs);
    }

    // for verifying if the Service is working or not
    @GetMapping("/health")
    public ResponseEntity < String > health(){

        return ResponseEntity.ok("Notification service is running");
    }
}


















































