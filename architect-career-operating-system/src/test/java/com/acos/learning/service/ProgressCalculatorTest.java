package com.acos.learning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.entity.LearningTopic;
import com.acos.learning.entity.TopicStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ProgressCalculator}. */
class ProgressCalculatorTest {

  @Test
  void shouldReturnZeroWhenNoTopics() {
    assertThat(ProgressCalculator.calculatePercent(List.of())).isZero();
    assertThat(ProgressCalculator.calculatePercent(0, 0)).isZero();
  }

  @Test
  void shouldCalculateRoundedPercentFromTopics() {
    LearningPlan plan =
        new LearningPlan(
            java.util.UUID.randomUUID(), "Plan", null, LearningPlanStatus.ACTIVE, null);
    LearningMilestone milestone = new LearningMilestone(plan, "M1", null, 0, null);
    LearningTopic completed = new LearningTopic(milestone, "T1", null, TopicStatus.COMPLETED, 0);
    LearningTopic inProgress = new LearningTopic(milestone, "T2", null, TopicStatus.IN_PROGRESS, 1);
    LearningTopic notStarted = new LearningTopic(milestone, "T3", null, TopicStatus.NOT_STARTED, 2);

    assertThat(ProgressCalculator.calculatePercent(List.of(completed, inProgress, notStarted)))
        .isEqualTo(33);
    assertThat(ProgressCalculator.countCompleted(List.of(completed, inProgress, notStarted)))
        .isEqualTo(1);
  }

  @Test
  void shouldCalculatePercentFromCounts() {
    assertThat(ProgressCalculator.calculatePercent(4, 2)).isEqualTo(50);
    assertThat(ProgressCalculator.calculatePercent(3, 1)).isEqualTo(33);
  }

  @Test
  void shouldRejectInvalidCounts() {
    assertThatThrownBy(() -> ProgressCalculator.calculatePercent(2, 3))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> ProgressCalculator.calculatePercent(2, -1))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
