package com.acos.learning.mapper;

import com.acos.learning.dto.LearningMilestoneResponse;
import com.acos.learning.dto.LearningPlanPageResponse;
import com.acos.learning.dto.LearningPlanResponse;
import com.acos.learning.dto.LearningPlanSummaryResponse;
import com.acos.learning.dto.LearningTopicResponse;
import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningTopic;
import com.acos.learning.service.ProgressCalculator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

/** MapStruct mappings between learning domain objects and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LearningMapper {

  /**
   * Maps a topic entity to a response DTO.
   *
   * @param topic topic entity
   * @return topic response
   */
  LearningTopicResponse toTopicResponse(LearningTopic topic);

  /**
   * Maps a milestone entity to a response DTO including progress.
   *
   * @param milestone milestone with topics initialized
   * @return milestone response
   */
  @Mapping(target = "topics", source = "topics")
  @Mapping(target = "totalTopics", source = "topics", qualifiedByName = "topicCount")
  @Mapping(target = "completedTopics", source = "topics", qualifiedByName = "completedTopicCount")
  @Mapping(target = "progressPercent", source = "topics", qualifiedByName = "progressPercent")
  LearningMilestoneResponse toMilestoneResponse(LearningMilestone milestone);

  /**
   * Maps a plan entity with details to a response DTO including progress.
   *
   * @param plan plan with milestones and topics initialized
   * @return plan response
   */
  @Mapping(target = "milestones", source = "milestones")
  @Mapping(target = "totalTopics", source = ".", qualifiedByName = "planTopicCount")
  @Mapping(target = "completedTopics", source = ".", qualifiedByName = "planCompletedTopicCount")
  @Mapping(target = "progressPercent", source = ".", qualifiedByName = "planProgressPercent")
  LearningPlanResponse toPlanResponse(LearningPlan plan);

  /**
   * Maps a plan to a summary response using precomputed topic counts.
   *
   * @param plan plan entity
   * @param totalTopics total topics
   * @param completedTopics completed topics
   * @return plan summary
   */
  default LearningPlanSummaryResponse toPlanSummary(
      LearningPlan plan, long totalTopics, long completedTopics) {
    return new LearningPlanSummaryResponse(
        plan.getId(),
        plan.getTitle(),
        plan.getDescription(),
        plan.getStatus(),
        plan.getTargetDate(),
        ProgressCalculator.calculatePercent(totalTopics, completedTopics),
        (int) totalTopics,
        (int) completedTopics,
        plan.getCreatedAt(),
        plan.getUpdatedAt());
  }

  /**
   * Maps a Spring Data page of summaries.
   *
   * @param page page of summaries
   * @return page response
   */
  default LearningPlanPageResponse toPageResponse(Page<LearningPlanSummaryResponse> page) {
    return new LearningPlanPageResponse(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  /**
   * Counts topics in a collection.
   *
   * @param topics topics
   * @return count
   */
  @Named("topicCount")
  default int topicCount(List<LearningTopic> topics) {
    return topics == null ? 0 : topics.size();
  }

  /**
   * Counts completed topics.
   *
   * @param topics topics
   * @return completed count
   */
  @Named("completedTopicCount")
  default int completedTopicCount(List<LearningTopic> topics) {
    return topics == null ? 0 : ProgressCalculator.countCompleted(topics);
  }

  /**
   * Calculates progress percent from topics.
   *
   * @param topics topics
   * @return progress percent
   */
  @Named("progressPercent")
  default int progressPercent(List<LearningTopic> topics) {
    return topics == null ? 0 : ProgressCalculator.calculatePercent(topics);
  }

  /**
   * Counts all topics under a plan.
   *
   * @param plan plan with milestones and topics
   * @return topic count
   */
  @Named("planTopicCount")
  default int planTopicCount(LearningPlan plan) {
    return flattenTopics(plan).size();
  }

  /**
   * Counts completed topics under a plan.
   *
   * @param plan plan with milestones and topics
   * @return completed count
   */
  @Named("planCompletedTopicCount")
  default int planCompletedTopicCount(LearningPlan plan) {
    return ProgressCalculator.countCompleted(flattenTopics(plan));
  }

  /**
   * Calculates plan progress percent.
   *
   * @param plan plan with milestones and topics
   * @return progress percent
   */
  @Named("planProgressPercent")
  default int planProgressPercent(LearningPlan plan) {
    return ProgressCalculator.calculatePercent(flattenTopics(plan));
  }

  private static List<LearningTopic> flattenTopics(LearningPlan plan) {
    if (plan == null || plan.getMilestones() == null) {
      return List.of();
    }
    return plan.getMilestones().stream()
        .flatMap(milestone -> milestone.getTopics().stream())
        .toList();
  }
}
