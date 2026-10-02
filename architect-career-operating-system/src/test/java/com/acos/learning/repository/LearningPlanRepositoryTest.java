package com.acos.learning.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.entity.LearningTopic;
import com.acos.learning.entity.TopicStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Repository slice tests for learning persistence and progress counts. */
class LearningPlanRepositoryTest extends LearningRepositoryTestSupport {

  @Autowired private LearningPlanRepository learningPlanRepository;
  @Autowired private LearningMilestoneRepository learningMilestoneRepository;
  @Autowired private LearningTopicRepository learningTopicRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void shouldPersistPlanHierarchyAndCountProgress() {
    User owner =
        userRepository.saveAndFlush(
            new User(
                "learning-owner@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Ada",
                "Lovelace"));

    LearningPlan plan =
        new LearningPlan(owner.getId(), "System Design", null, LearningPlanStatus.ACTIVE, null);
    plan = learningPlanRepository.saveAndFlush(plan);

    LearningMilestone milestone = new LearningMilestone(plan, "Consistency", null, 0, null);
    plan.addMilestone(milestone);
    learningMilestoneRepository.saveAndFlush(milestone);

    LearningTopic completed = new LearningTopic(milestone, "CAP", null, TopicStatus.COMPLETED, 0);
    LearningTopic pending =
        new LearningTopic(milestone, "Quorum", null, TopicStatus.NOT_STARTED, 1);
    milestone.addTopic(completed);
    milestone.addTopic(pending);
    learningTopicRepository.saveAndFlush(completed);
    learningTopicRepository.saveAndFlush(pending);

    assertThat(learningMilestoneRepository.countByPlanId(plan.getId())).isEqualTo(1);
    assertThat(learningPlanRepository.countTopicsByPlanId(plan.getId())).isEqualTo(2);
    assertThat(learningPlanRepository.countCompletedTopicsByPlanId(plan.getId())).isEqualTo(1);
    assertThat(
            learningMilestoneRepository
                .findWithTopicsByIdAndPlanIdAndOwnerId(
                    milestone.getId(), plan.getId(), owner.getId())
                .orElseThrow()
                .getTopics())
        .hasSize(2);
    assertThat(
            learningPlanRepository
                .findWithDetailsByIdAndOwnerId(plan.getId(), owner.getId())
                .orElseThrow()
                .getTitle())
        .isEqualTo("System Design");
  }
}
