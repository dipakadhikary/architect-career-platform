package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Immutable business-action audit trail entry for the career tracker. */
@Entity
@Table(name = "career_audit_logs", schema = "acos")
public class CareerAuditLog extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "actor_id", nullable = false, updatable = false)
  private UUID actorId;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 64)
  private CareerAuditAction action;

  @Column(name = "entity_type", nullable = false, length = 64)
  private String entityType;

  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @Column(name = "details", length = 2000)
  private String details;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  /** Creates an empty audit log entry for JPA. */
  protected CareerAuditLog() {}

  /**
   * Creates an audit log entry, timestamped at construction time.
   *
   * @param ownerId owning user id whose data was affected
   * @param actorId user id who performed the action
   * @param action business action performed
   * @param entityType affected entity type name
   * @param entityId affected entity id
   */
  public CareerAuditLog(
      UUID ownerId, UUID actorId, CareerAuditAction action, String entityType, UUID entityId) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.actorId = Objects.requireNonNull(actorId, "actorId must not be null");
    this.action = Objects.requireNonNull(action, "action must not be null");
    this.entityType = Objects.requireNonNull(entityType, "entityType must not be null");
    this.entityId = Objects.requireNonNull(entityId, "entityId must not be null");
    this.occurredAt = Instant.now();
  }

  /**
   * Returns the owning user id.
   *
   * @return owner id
   */
  public UUID getOwnerId() {
    return ownerId;
  }

  /**
   * Returns the user id who performed the action.
   *
   * @return actor id
   */
  public UUID getActorId() {
    return actorId;
  }

  /**
   * Returns the business action performed.
   *
   * @return action
   */
  public CareerAuditAction getAction() {
    return action;
  }

  /**
   * Returns the affected entity type name.
   *
   * @return entity type
   */
  public String getEntityType() {
    return entityType;
  }

  /**
   * Returns the affected entity id.
   *
   * @return entity id
   */
  public UUID getEntityId() {
    return entityId;
  }

  /**
   * Returns optional additional details.
   *
   * @return details, may be {@code null}
   */
  public String getDetails() {
    return details;
  }

  /**
   * Updates optional additional details.
   *
   * @param details new details
   */
  public void setDetails(String details) {
    this.details = details;
  }

  /**
   * Returns the timestamp when the action occurred.
   *
   * @return occurred-at instant
   */
  public Instant getOccurredAt() {
    return occurredAt;
  }
}
