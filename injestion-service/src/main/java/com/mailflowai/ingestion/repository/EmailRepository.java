package com.mailflowai.ingestion.repository;

import com.mailflowai.ingestion.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailRepository extends JpaRepository<Email, UUID>
{
    Optional <Email> findByGmailMessageId(String gmailMessageId);

    boolean existsByGmailMessageId(String gmailMessageId);

}

