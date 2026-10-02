package com.acos.learning.service;

import com.acos.learning.dto.LearningTopicRequest;
import com.acos.learning.dto.LearningTopicResponse;
import com.acos.learning.dto.TopicStatusUpdateRequest;
import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningTopic;
import com.acos.learning.entity.TopicStatus;
import com.acos.learning.exception.DuplicateSortOrderException;
import com.acos.learning.exception.LearningMilestoneNotFoundException;
import com.acos.learning.exception.LearningTopicNotFoundException;
import com.acos.learning.mapper.LearningMapper;
import com.acos.learning.repository.LearningMilestoneRepository;
import com.acos.learning.repository.LearningTopicRepository;
import com.acos.learning.validator.LearningValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link LearningTopicService} implementation. */
@Service
@Transactional
public class LearningTopicServiceImpl implements LearningTopicService {

  private final LearningMilestoneRepository learningMilestoneRepository;
  private final LearningTopicRepository learningTopicRepository;
  private final LearningMapper learningMapper;
  private final LearningValidator learningValidator;

  /**
   * Creates the learning topic service.
   *
   * @param learningMilestoneRepository milestone repository
   * @param learningTopicRepository topic repository
   * @param learningMapper mapper
   * @param learningValidator validator
   */
  public LearningTopicServiceImpl(
      LearningMilestoneRepository learningMilestoneRepository,
      LearningTopicRepository learningTopicRepository,
      LearningMapper learningMapper,
      LearningValidator learningValidator) {
    this.learningMilestoneRepository =
        Objects.requireNonNull(
            learningMilestoneRepository, "learningMilestoneRepository must not be null");
    this.learningTopicRepository =
        Objects.requireNonNull(learningTopicRepository, "learningTopicRepository must not be null");
    this.learningMapper = Objects.requireNonNull(learningMapper, "learningMapper must not be null");
    this.learningValidator =
        Objects.requireNonNull(learningValidator, "learningValidator must not be null");
  }

  @Override
  public LearningTopicResponse create(
      UUID ownerId, UUID planId, UUID milestoneId, LearningTopicRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningMilestone milestone = requireOwnedMilestone(ownerId, planId, milestoneId);
    learningValidator.validateTopicCapacity(
        learningTopicRepository.countByMilestoneId(milestoneId));
    int sortOrder =
        learningValidator.resolveSortOrder(
            request.sortOrder(),
            learningTopicRepository.findMaxSortOrderByMilestoneId(milestoneId));
    TopicStatus status = request.status() == null ? TopicStatus.NOT_STARTED : request.status();

    LearningTopic topic =
        new LearningTopic(
            milestone,
            request.title().trim(),
            learningValidator.normalizeDescription(request.description()),
            status,
            sortOrder);
    milestone.addTopic(topic);
    try {
      LearningTopic saved = learningTopicRepository.saveAndFlush(topic);
      return learningMapper.toTopicResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateSortOrderException(
          "Topic sortOrder " + sortOrder + " already exists for this milestone", ex);
    }
  }

  @Override
  public LearningTopicResponse update(
      UUID ownerId, UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningTopic topic = requireOwnedTopic(ownerId, planId, milestoneId, topicId);
    topic.setTitle(request.title().trim());
    topic.setDescription(learningValidator.normalizeDescription(request.description()));
    if (request.status() != null) {
      topic.setStatus(request.status());
    }
    if (request.sortOrder() != null) {
      topic.setSortOrder(
          learningValidator.resolveSortOrder(request.sortOrder(), java.util.Optional.empty()));
    }
    try {
      learningTopicRepository.flush();
      return learningMapper.toTopicResponse(topic);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateSortOrderException(
          "Topic sortOrder " + topic.getSortOrder() + " already exists for this milestone", ex);
    }
  }

  @Override
  public void delete(UUID ownerId, UUID planId, UUID milestoneId, UUID topicId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    Objects.requireNonNull(topicId, "topicId must not be null");

    LearningTopic topic = requireOwnedTopic(ownerId, planId, milestoneId, topicId);
    LearningMilestone milestone = topic.getMilestone();
    milestone.removeTopic(topic);
    learningTopicRepository.delete(topic);
  }

  @Override
  public LearningTopicResponse updateStatus(
      UUID ownerId, UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningTopic topic = requireOwnedTopic(ownerId, planId, milestoneId, topicId);
    topic.setStatus(request.status());
    return learningMapper.toTopicResponse(topic);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningTopicResponse> list(UUID ownerId, UUID planId, UUID milestoneId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    requireOwnedMilestone(ownerId, planId, milestoneId);
    return learningTopicRepository
        .findByMilestoneIdAndPlanIdAndOwnerIdOrderBySortOrderAsc(milestoneId, planId, ownerId)
        .stream()
        .map(learningMapper::toTopicResponse)
        .toList();
  }

  private LearningMilestone requireOwnedMilestone(UUID ownerId, UUID planId, UUID milestoneId) {
    return learningMilestoneRepository
        .findByIdAndPlanIdAndOwnerId(milestoneId, planId, ownerId)
        .orElseThrow(() -> new LearningMilestoneNotFoundException(milestoneId));
  }

  private LearningTopic requireOwnedTopic(
      UUID ownerId, UUID planId, UUID milestoneId, UUID topicId) {
    return learningTopicRepository
        .findByIdAndMilestoneIdAndPlanIdAndOwnerId(topicId, milestoneId, planId, ownerId)
        .orElseThrow(() -> new LearningTopicNotFoundException(topicId));
  }
}
