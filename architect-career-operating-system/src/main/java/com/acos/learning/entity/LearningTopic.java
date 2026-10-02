package com.acos.learning.entity;

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
import java.util.Objects;

/** Ordered topic within a learning milestone. */
@Entity
@Table(name = "learning_topics", schema = "acos")
public class LearningTopic extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "milestone_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_learning_topics_milestone_id"))
  private LearningMilestone milestone;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", length = 2000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private TopicStatus status;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  /** Creates an empty topic for JPA. */
  protected LearningTopic() {}

  /**
   * Creates a learning topic.
   *
   * @param milestone owning milestone
   * @param title topic title
   * @param description optional description
   * @param status topic status
   * @param sortOrder display order within the milestone
   */
  public LearningTopic(
      LearningMilestone milestone,
      String title,
      String description,
      TopicStatus status,
      int sortOrder) {
    this.milestone = Objects.requireNonNull(milestone, "milestone must not be null");
    this.title = Objects.requireNonNull(title, "title must not be null");
    this.description = description;
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.sortOrder = sortOrder;
  }

  /**
   * Returns the owning milestone.
   *
   * @return milestone
   */
  public LearningMilestone getMilestone() {
    return milestone;
  }

  /**
   * Assigns the owning milestone.
   *
   * @param milestone owning milestone, may be {@code null} when unlinking
   */
  void setMilestone(LearningMilestone milestone) {
    this.milestone = milestone;
  }

  /**
   * Returns the topic title.
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Updates the topic title.
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
   * Returns the topic status.
   *
   * @return status
   */
  public TopicStatus getStatus() {
    return status;
  }

  /**
   * Updates the topic status.
   *
   * @param status new status
   */
  public void setStatus(TopicStatus status) {
    this.status = Objects.requireNonNull(status, "status must not be null");
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
}
