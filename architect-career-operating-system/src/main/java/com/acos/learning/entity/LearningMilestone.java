package com.acos.learning.entity;

import com.acos.common.persistence.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.BatchSize;

/** Ordered milestone within a learning plan. */
@Entity
@Table(name = "learning_milestones", schema = "acos")
public class LearningMilestone extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "plan_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_learning_milestones_plan_id"))
  private LearningPlan plan;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", length = 2000)
  private String description;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(name = "target_date")
  private LocalDate targetDate;

  @OneToMany(
      mappedBy = "milestone",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 25)
  private final List<LearningTopic> topics = new ArrayList<>();

  /** Creates an empty milestone for JPA. */
  protected LearningMilestone() {}

  /**
   * Creates a learning milestone.
   *
   * @param plan owning plan
   * @param title milestone title
   * @param description optional description
   * @param sortOrder display order within the plan
   * @param targetDate optional target date
   */
  public LearningMilestone(
      LearningPlan plan, String title, String description, int sortOrder, LocalDate targetDate) {
    this.plan = Objects.requireNonNull(plan, "plan must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.description = description;
    this.sortOrder = sortOrder;
    this.targetDate = targetDate;
  }

  /**
   * Returns the owning plan.
   *
   * @return plan
   */
  public LearningPlan getPlan() {
    return plan;
  }

  /**
   * Assigns the owning plan.
   *
   * @param plan owning plan, may be {@code null} when unlinking
   */
  void setPlan(LearningPlan plan) {
    this.plan = plan;
  }

  /**
   * Returns the milestone title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the milestone title.
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
   * Returns the sort order.
   *
   * @return sort order
   */
  public int getSortOrder() {
    return sortOrder;
  }

  /**
   * Updates the sort order.
   *
   * @param sortOrder new sort order
   */
  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
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
   * Returns an unmodifiable view of topics.
   *
   * @return topics ordered by sort order
   */
  public List<LearningTopic> getTopics() {
    return Collections.unmodifiableList(topics);
  }

  /**
   * Adds a topic to this milestone.
   *
   * @param topic topic belonging to this milestone
   */
  public void addTopic(LearningTopic topic) {
    Objects.requireNonNull(topic, "topic must not be null");
    topics.add(topic);
    topic.setMilestone(this);
  }

  /**
   * Removes a topic from this milestone.
   *
   * @param topic topic to remove
   */
  public void removeTopic(LearningTopic topic) {
    Objects.requireNonNull(topic, "topic must not be null");
    topics.remove(topic);
    topic.setMilestone(null);
  }
}
