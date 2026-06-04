package com.mailflowai.routing.repository;

import com.mailflowai.routing.model.Routing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface RoutingRepository extends JpaRepository <Routing , UUID> {

    Optional < Routing > findByEmailId(UUID emailId);
    boolean existsByEmailId(UUID emailId);
}
