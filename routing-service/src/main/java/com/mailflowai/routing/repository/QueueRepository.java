package com.mailflowai.routing.repository;

import com.mailflowai.routing.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueRepository extends JpaRepository < Queue, UUID > {

    Optional <Queue> findByCategory(String category);
}
