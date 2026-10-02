package com.acos.learning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Learning roadmap configuration limits.
 *
 * @param maxPageSize maximum allowed page size for list APIs
 * @param maxMilestonesPerPlan maximum milestones allowed per plan
 * @param maxTopicsPerMilestone maximum topics allowed per milestone
 */
@ConfigurationProperties(prefix = "acos.learning")
public record LearningProperties(
    int maxPageSize, int maxMilestonesPerPlan, int maxTopicsPerMilestone) {

  /**
   * Creates learning properties with validation.
   *
   * @param maxPageSize max page size
   * @param maxMilestonesPerPlan max milestones per plan
   * @param maxTopicsPerMilestone max topics per milestone
   */
  public LearningProperties {
    if (maxPageSize <= 0) {
      throw new IllegalArgumentException("maxPageSize must be positive");
    }
    if (maxMilestonesPerPlan <= 0) {
      throw new IllegalArgumentException("maxMilestonesPerPlan must be positive");
    }
    if (maxTopicsPerMilestone <= 0) {
      throw new IllegalArgumentException("maxTopicsPerMilestone must be positive");
    }
  }
}
