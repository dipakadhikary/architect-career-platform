package com.acos.career.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Immutable record of a single application status transition. */
@Entity
@Table(name = "career_application_status_history", schema = "acos")
public class ApplicationStatusHistory extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "application_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_career_app_status_history_application_id"))
  private JobApplication application;

  @Enumerated(EnumType.STRING)
  @Column(name = "old_status", length = 40)
  private ApplicationStatus oldStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "new_status", nullable = false, length = 40)
  private ApplicationStatus newStatus;

  @Column(name = "changed_at", nullable = false)
  private Instant changedAt;

  @Column(name = "changed_by", nullable = false)
  private UUID changedBy;

  @Column(name = "comments", length = 2000)
  private String comments;

  @Column(name = "archived", nullable = false)
  private boolean archived;

  @Column(name = "archived_at")
  private Instant archivedAt;

  /** Creates an empty status history entry for JPA. */
  protected ApplicationStatusHistory() {}

  /**
   * Creates a status history entry, timestamped at construction time.
   *
   * @param application owning application
   * @param oldStatus previous status, {@code null} when this is the initial entry
   * @param newStatus resulting status
   * @param changedBy user id who performed the transition
   */
  public ApplicationStatusHistory(
      JobApplication application,
      ApplicationStatus oldStatus,
      ApplicationStatus newStatus,
      UUID changedBy) {
    this.application = Objects.requireNonNull(application, "application must not be null");
    this.oldStatus = oldStatus;
    this.newStatus = Objects.requireNonNull(newStatus, "newStatus must not be null");
    this.changedBy = Objects.requireNonNull(changedBy, "changedBy must not be null");
    this.changedAt = Instant.now();
    this.archived = false;
  }

  /**
   * Returns the owning job application.
   *
   * @return application
   */
  public JobApplication getApplication() {
    return application;
  }

  /**
   * Returns the previous status.
   *
   * @return old status, {@code null} for the initial entry
   */
  public ApplicationStatus getOldStatus() {
    return oldStatus;
  }

  /**
   * Returns the resulting status.
   *
   * @return new status
   */
  public ApplicationStatus getNewStatus() {
    return newStatus;
  }

  /**
   * Returns the transition timestamp.
   *
   * @return changed-at instant
   */
  public Instant getChangedAt() {
    return changedAt;
  }

  /**
   * Returns the user id who performed the transition.
   *
   * @return changed-by user id
   */
  public UUID getChangedBy() {
    return changedBy;
  }

  /**
   * Returns optional comments describing the transition.
   *
   * @return comments, may be {@code null}
   */
  public String getComments() {
    return comments;
  }

  /**
   * Updates optional comments describing the transition.
   *
   * @param comments new comments
   */
  public void setComments(String comments) {
    this.comments = comments;
  }

  /**
   * Reports whether this history entry has been soft-deleted.
   *
   * @return {@code true} when archived
   */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Returns the archival timestamp.
   *
   * @return archived-at instant, may be {@code null}
   */
  public Instant getArchivedAt() {
    return archivedAt;
  }

  /** Soft-deletes this history entry. */
  public void archive() {
    this.archived = true;
    this.archivedAt = Instant.now();
  }
}
