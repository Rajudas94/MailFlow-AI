package com.mailflowai.routing.controller;

// import com.mailflowai.classification.dto.EmailEvent;
// import com.mailflowai.classification.service.ClassificationService;
import com.mailflowai.routing.dto.ClassificationEvent;
import com.mailflowai.routing.dto.RoutingEvent;
import com.mailflowai.routing.service.RoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routing")
@RequiredArgsConstructor
@Slf4j
public class RoutingController {

    private final RoutingService routingService;

    @PostMapping("/test")
    public ResponseEntity < String > testRouting(@RequestBody ClassificationEvent emailEvent)
    {
        log.info("Manual routing test triggered for subject : {}", emailEvent.getSubject());
        routingService.routeEmail(emailEvent);

        return ResponseEntity.ok("Routing triggered for : " + emailEvent.getSubject() );
    }

    // for verifying if the Service is working or not
    @GetMapping("/health")
    public ResponseEntity < String > health()
    {
        return ResponseEntity.ok("Routing service is running");
    }
}















































