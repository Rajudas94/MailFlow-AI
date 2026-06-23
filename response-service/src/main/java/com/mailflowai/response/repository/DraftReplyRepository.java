package com.mailflowai.response.repository;

import com.mailflowai.response.model.DraftReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface DraftReplyRepository extends JpaRepository < DraftReply, UUID > {

    // check if draft already exists for this email
    boolean existsByEmailId(UUID emailId);

    // Find draft by email — useful for approval workflow later
    Optional <DraftReply> findByEmailId(UUID emailId);

    // Find all drafts pending approval — useful for controller endpoint
    List <DraftReply> findByApprovalStatus(String approvalStatus);
}

