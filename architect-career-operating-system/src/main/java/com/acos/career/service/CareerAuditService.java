package com.acos.career.service;

import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.CareerAuditLog;
import com.acos.career.repository.CareerAuditLogRepository;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Records business-action audit trail entries for the career tracker. */
@Service
@Transactional
public class CareerAuditService {

  private final CareerAuditLogRepository careerAuditLogRepository;

  /**
   * Creates the audit service.
   *
   * @param careerAuditLogRepository audit log repository
   */
  public CareerAuditService(CareerAuditLogRepository careerAuditLogRepository) {
    this.careerAuditLogRepository =
        Objects.requireNonNull(
            careerAuditLogRepository, "careerAuditLogRepository must not be null");
  }

  /**
   * Records a business-action audit entry.
   *
   * @param ownerId owning user id whose data was affected
   * @param actorId user id who performed the action
   * @param action business action performed
   * @param entityType affected entity type name
   * @param entityId affected entity id
   * @param details optional additional details
   */
  public void record(
      UUID ownerId,
      UUID actorId,
      CareerAuditAction action,
      String entityType,
      UUID entityId,
      String details) {
    CareerAuditLog log = new CareerAuditLog(ownerId, actorId, action, entityType, entityId);
    log.setDetails(details);
    careerAuditLogRepository.save(log);
  }
}
