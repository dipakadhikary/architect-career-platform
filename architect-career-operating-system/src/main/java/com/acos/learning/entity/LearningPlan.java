package com.acos.learning.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.BatchSize;

/** User-owned learning roadmap plan. */
@Entity
@Table(name = "learning_plans", schema = "acos")
public class LearningPlan extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", length = 2000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private LearningPlanStatus status;

  @Column(name = "target_date")
  private LocalDate targetDate;

  @OneToMany(
      mappedBy = "plan",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 25)
  private final List<LearningMilestone> milestones = new ArrayList<>();

  /** Creates an empty plan for JPA. */
  protected LearningPlan() {}

  /**
   * Creates a learning plan.
   *
   * @param ownerId owning user id
   * @param title plan title
   * @param description optional description
   * @param status plan status
   * @param targetDate optional target completion date
   */
  public LearningPlan(
      UUID ownerId,
      String title,
      String description,
      LearningPlanStatus status,
      LocalDate targetDate) {
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.description = description;
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.targetDate = targetDate;
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
   * Returns the plan title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the plan title.
   *
   * @param title new title
   */
  public void setTitle(String title) {
    this.title = Objects.requireNonNull(title, "title must not be null");
  }

  /**
   * Returns the optional description.
   *
   * @return description, may be {@code null}
   */
  public String getDescription() {
    return description;
  }

  /**
   * Updates the optional description.
   *
   * @param description new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the plan status.
   *
   * @return status
   */
  public LearningPlanStatus getStatus() {
    return status;
  }

  /**
   * Updates the plan status.
   *
   * @param status new status
   */
  public void setStatus(LearningPlanStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  /**
   * Returns the optional target date.
   *
   * @return target date, may be {@code null}
   */
  public LocalDate getTargetDate() {
    return targetDate;
  }

  /**
   * Updates the optional target date.
   *
   * @param targetDate new target date
   */
  public void setTargetDate(LocalDate targetDate) {
    this.targetDate = targetDate;
  }

  /**
   * Returns an unmodifiable view of milestones.
   *
   * @return milestones ordered by sort order
   */
  public List<LearningMilestone> getMilestones() {
    return Collections.unmodifiableList(milestones);
  }

  /**
   * Adds a milestone to this plan.
   *
   * @param milestone milestone belonging to this plan
   */
  public void addMilestone(LearningMilestone milestone) {
    Objects.requireNonNull(milestone, "milestone must not be null");
    milestones.add(milestone);
    milestone.setPlan(this);
  }

  /**
   * Removes a milestone from this plan.
   *
   * @param milestone milestone to remove
   */
  public void removeMilestone(LearningMilestone milestone) {
    Objects.requireNonNull(milestone, "milestone must not be null");
    milestones.remove(milestone);
    milestone.setPlan(null);
  }
}
