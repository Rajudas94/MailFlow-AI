package com.mailflowai.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mailflowai.notification.model.AuditLog;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository < AuditLog, UUID >
{
    boolean existsByEmailId(UUID emailId);
    List <AuditLog> findByEmailId(UUID emailId);
}
