package com.acos.learning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.learning.dto.LearningTopicRequest;
import com.acos.learning.dto.LearningTopicResponse;
import com.acos.learning.dto.TopicStatusUpdateRequest;
import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.entity.LearningTopic;
import com.acos.learning.entity.TopicStatus;
import com.acos.learning.mapper.LearningMapper;
import com.acos.learning.repository.LearningMilestoneRepository;
import com.acos.learning.repository.LearningTopicRepository;
import com.acos.learning.validator.LearningValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LearningTopicServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class LearningTopicServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID PLAN_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final UUID MILESTONE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  private static final UUID TOPIC_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

  @Mock private LearningMilestoneRepository learningMilestoneRepository;
  @Mock private LearningTopicRepository learningTopicRepository;
  @Mock private LearningMapper learningMapper;
  @Mock private LearningValidator learningValidator;

  private LearningTopicServiceImpl learningTopicService;

  @BeforeEach
  void setUp() {
    learningTopicService =
        new LearningTopicServiceImpl(
            learningMilestoneRepository,
            learningTopicRepository,
            learningMapper,
            learningValidator);
  }

  @Test
  void shouldCreateTopicWithDefaultStatus() {
    LearningPlan plan = new LearningPlan(OWNER_ID, "Plan", null, LearningPlanStatus.ACTIVE, null);
    LearningMilestone milestone = new LearningMilestone(plan, "M1", null, 0, null);
    LearningTopicRequest request = new LearningTopicRequest(" CAP ", " desc ", null, null);
    LearningTopicResponse expected =
        new LearningTopicResponse(
            TOPIC_ID,
            "CAP",
            "desc",
            TopicStatus.NOT_STARTED,
            0,
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));

    when(learningMilestoneRepository.findByIdAndPlanIdAndOwnerId(MILESTONE_ID, PLAN_ID, OWNER_ID))
        .thenReturn(Optional.of(milestone));
    when(learningTopicRepository.countByMilestoneId(MILESTONE_ID)).thenReturn(0L);
    when(learningTopicRepository.findMaxSortOrderByMilestoneId(MILESTONE_ID))
        .thenReturn(Optional.empty());
    when(learningValidator.resolveSortOrder(eq(null), any())).thenReturn(0);
    when(learningValidator.normalizeDescription(" desc ")).thenReturn("desc");
    when(learningTopicRepository.saveAndFlush(any(LearningTopic.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(learningMapper.toTopicResponse(any(LearningTopic.class))).thenReturn(expected);

    LearningTopicResponse response =
        learningTopicService.create(OWNER_ID, PLAN_ID, MILESTONE_ID, request);

    assertThat(response.status()).isEqualTo(TopicStatus.NOT_STARTED);
    verify(learningValidator).validateTopicCapacity(0L);
  }

  @Test
  void shouldUpdateTopicStatus() {
    LearningPlan plan = new LearningPlan(OWNER_ID, "Plan", null, LearningPlanStatus.ACTIVE, null);
    LearningMilestone milestone = new LearningMilestone(plan, "M1", null, 0, null);
    LearningTopic topic = new LearningTopic(milestone, "CAP", null, TopicStatus.NOT_STARTED, 0);
    LearningTopicResponse expected =
        new LearningTopicResponse(
            TOPIC_ID,
            "CAP",
            null,
            TopicStatus.COMPLETED,
            0,
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T07:00:00Z"));

    when(learningTopicRepository.findByIdAndMilestoneIdAndPlanIdAndOwnerId(
            TOPIC_ID, MILESTONE_ID, PLAN_ID, OWNER_ID))
        .thenReturn(Optional.of(topic));
    when(learningMapper.toTopicResponse(topic)).thenReturn(expected);

    LearningTopicResponse response =
        learningTopicService.updateStatus(
            OWNER_ID,
            PLAN_ID,
            MILESTONE_ID,
            TOPIC_ID,
            new TopicStatusUpdateRequest(TopicStatus.COMPLETED));

    assertThat(topic.getStatus()).isEqualTo(TopicStatus.COMPLETED);
    assertThat(response.status()).isEqualTo(TopicStatus.COMPLETED);
  }
}
