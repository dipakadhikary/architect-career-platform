package com.acos.learning.service;

import com.acos.learning.entity.LearningTopic;
import com.acos.learning.entity.TopicStatus;
import java.util.Collection;
import java.util.Objects;

/** Calculates learning progress percentages from topic completion. */
public final class ProgressCalculator {

  private ProgressCalculator() {}

  /**
   * Calculates completion percentage from topics.
   *
   * <p>Formula: {@code round(completedTopics / totalTopics * 100)}. Returns {@code 0} when there
   * are no topics.
   *
   * @param topics topics to evaluate
   * @return progress percentage from 0 to 100
   */
  public static int calculatePercent(Collection<LearningTopic> topics) {
    Objects.requireNonNull(topics, "topics must not be null");
    if (topics.isEmpty()) {
      return 0;
    }
    long completed =
        topics.stream().filter(topic -> topic.getStatus() == TopicStatus.COMPLETED).count();
    return (int) Math.round(completed * 100.0 / topics.size());
  }

  /**
   * Calculates completion percentage from counts.
   *
   * @param totalTopics total topic count
   * @param completedTopics completed topic count
   * @return progress percentage from 0 to 100
   */
  public static int calculatePercent(long totalTopics, long completedTopics) {
    if (totalTopics <= 0) {
      return 0;
    }
    if (completedTopics < 0) {
      throw new IllegalArgumentException("completedTopics must not be negative");
    }
    if (completedTopics > totalTopics) {
      throw new IllegalArgumentException("completedTopics must not exceed totalTopics");
    }
    return (int) Math.round(completedTopics * 100.0 / totalTopics);
  }

  /**
   * Counts completed topics.
   *
   * @param topics topics to evaluate
   * @return completed count
   */
  public static int countCompleted(Collection<LearningTopic> topics) {
    Objects.requireNonNull(topics, "topics must not be null");
    return (int)
        topics.stream().filter(topic -> topic.getStatus() == TopicStatus.COMPLETED).count();
  }
}
