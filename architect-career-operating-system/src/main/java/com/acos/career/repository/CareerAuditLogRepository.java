package com.acos.career.repository;

import com.acos.career.entity.CareerAuditLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link CareerAuditLog}. */
public interface CareerAuditLogRepository extends JpaRepository<CareerAuditLog, UUID> {}
