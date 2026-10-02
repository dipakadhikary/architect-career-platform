package com.acos.learning.entity;

/** Lifecycle status of a learning plan. */
public enum LearningPlanStatus {
  /** Plan is being drafted and is not yet actively pursued. */
  DRAFT,
  /** Plan is actively in progress. */
  ACTIVE,
  /** All planned work is complete. */
  COMPLETED,
  /** Plan is archived and no longer active. */
  ARCHIVED
}
