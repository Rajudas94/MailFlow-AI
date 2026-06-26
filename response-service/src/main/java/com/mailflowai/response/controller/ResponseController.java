package com.mailflowai.response.controller;

import com.mailflowai.response.model.DraftReply;
import com.mailflowai.response.service.ResponseService;
import com.mailflowai.response.dto.RoutingEvent;
import com.mailflowai.response.repository.DraftReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/response")
@RequiredArgsConstructor
@Slf4j
public class ResponseController {

    private final ResponseService responseService;
    private final DraftReplyRepository draftReplyRepository;

    // for fetching the list of records marked as "PENDING" from database
    @GetMapping("/pending")
    public ResponseEntity <List<DraftReply>> findApprovalStatus()
    {
        List < DraftReply > draft = draftReplyRepository.findByApprovalStatus("PENDING");
        return ResponseEntity.ok(draft);
    }

    // for verifying if the Service is working or not
    @GetMapping("/health")
    public ResponseEntity < String > health(){ return ResponseEntity.ok("Response service is running"); }

    // for checking which email has been "Approved"
    @PutMapping("/approve/{emailId}")
    public ResponseEntity <String> approveDraft(@PathVariable UUID emailId)
    {
        // Find the draft using emailId
        Optional < DraftReply > draftOptional = draftReplyRepository.findByEmailId(emailId);

        // Since "draftOptional" is Optional , we check if it exists
        if(draftOptional.isEmpty()){ return ResponseEntity.notFound().build(); }

        // if it exists, get the Database object from the draftOptional
        DraftReply draft = draftOptional.get();

        // change the approval status from pending to approved
        draft.setApprovalStatus("APPROVED");

        // save the changes
        draftReplyRepository.save(draft);

        // return response entity with a success message
        return ResponseEntity.ok("Draft approved for emailId : " + emailId);
    }

    @PutMapping("/reject/{emailId}")
    public ResponseEntity<String> rejectDraft(@PathVariable UUID emailId) {

        // Find the draft using emailId
        Optional < DraftReply > draftOptional = draftReplyRepository.findByEmailId(emailId);

        // Since "draftOptional" is Optional , we check if it exists
        if(draftOptional.isEmpty()){ return ResponseEntity.notFound().build(); }

        // if it exists, get the Database object from the draftOptional
        DraftReply draft = draftOptional.get();

        // change the approval status from pending to approved
        draft.setApprovalStatus("REJECTED");

        // save the changes
        draftReplyRepository.save(draft);

        // return response entity with a success message
        return ResponseEntity.ok("Draft rejected for emailId : " + emailId);
    }
}


















































