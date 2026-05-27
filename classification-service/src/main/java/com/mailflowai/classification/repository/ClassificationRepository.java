package com.mailflowai.classification.repository;

import com.mailflowai.classification.model.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

public interface ClassificationRepository extends JpaRepository < Classification, UUID > {

    Optional < Classification > findByEmailId(UUID emailId);
    boolean existsByEmailId(UUID emailId);
}
