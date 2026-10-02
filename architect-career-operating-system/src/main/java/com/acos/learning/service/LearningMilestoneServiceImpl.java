package com.acos.learning.service;

import com.acos.learning.dto.LearningMilestoneRequest;
import com.acos.learning.dto.LearningMilestoneResponse;
import com.acos.learning.entity.LearningMilestone;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.exception.DuplicateSortOrderException;
import com.acos.learning.exception.LearningMilestoneNotFoundException;
import com.acos.learning.exception.LearningPlanNotFoundException;
import com.acos.learning.mapper.LearningMapper;
import com.acos.learning.repository.LearningMilestoneRepository;
import com.acos.learning.repository.LearningPlanRepository;
import com.acos.learning.validator.LearningValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link LearningMilestoneService} implementation. */
@Service
@Transactional
public class LearningMilestoneServiceImpl implements LearningMilestoneService {

  private final LearningPlanRepository learningPlanRepository;
  private final LearningMilestoneRepository learningMilestoneRepository;
  private final LearningMapper learningMapper;
  private final LearningValidator learningValidator;

  /**
   * Creates the learning milestone service.
   *
   * @param learningPlanRepository plan repository
   * @param learningMilestoneRepository milestone repository
   * @param learningMapper mapper
   * @param learningValidator validator
   */
  public LearningMilestoneServiceImpl(
      LearningPlanRepository learningPlanRepository,
      LearningMilestoneRepository learningMilestoneRepository,
      LearningMapper learningMapper,
      LearningValidator learningValidator) {
    this.learningPlanRepository =
        Objects.requireNonNull(learningPlanRepository, "learningPlanRepository must not be null");
    this.learningMilestoneRepository =
        Objects.requireNonNull(
            learningMilestoneRepository, "learningMilestoneRepository must not be null");
    this.learningMapper = Objects.requireNonNull(learningMapper, "learningMapper must not be null");
    this.learningValidator =
        Objects.requireNonNull(learningValidator, "learningValidator must not be null");
  }

  @Override
  public LearningMilestoneResponse create(
      UUID ownerId, UUID planId, LearningMilestoneRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningPlan plan = requireOwnedPlan(ownerId, planId);
    learningValidator.validateMilestoneCapacity(learningMilestoneRepository.countByPlanId(planId));
    int sortOrder =
        learningValidator.resolveSortOrder(
            request.sortOrder(), learningMilestoneRepository.findMaxSortOrderByPlanId(planId));

    LearningMilestone milestone =
        new LearningMilestone(
            plan,
            request.title().trim(),
            learningValidator.normalizeDescription(request.description()),
            sortOrder,
            request.targetDate());
    plan.addMilestone(milestone);
    try {
      LearningMilestone saved = learningMilestoneRepository.saveAndFlush(milestone);
      return learningMapper.toMilestoneResponse(saved);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateSortOrderException(
          "Milestone sortOrder " + sortOrder + " already exists for this plan", ex);
    }
  }

  @Override
  public LearningMilestoneResponse update(
      UUID ownerId, UUID planId, UUID milestoneId, LearningMilestoneRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningMilestone milestone = requireOwnedMilestoneWithTopics(ownerId, planId, milestoneId);
    milestone.setTitle(request.title().trim());
    milestone.setDescription(learningValidator.normalizeDescription(request.description()));
    milestone.setTargetDate(request.targetDate());
    if (request.sortOrder() != null) {
      milestone.setSortOrder(
          learningValidator.resolveSortOrder(request.sortOrder(), java.util.Optional.empty()));
    }
    try {
      learningMilestoneRepository.flush();
      return learningMapper.toMilestoneResponse(milestone);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateSortOrderException(
          "Milestone sortOrder " + milestone.getSortOrder() + " already exists for this plan", ex);
    }
  }

  @Override
  public void delete(UUID ownerId, UUID planId, UUID milestoneId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");

    LearningMilestone milestone =
        learningMilestoneRepository
            .findByIdAndPlanIdAndOwnerId(milestoneId, planId, ownerId)
            .orElseThrow(() -> new LearningMilestoneNotFoundException(milestoneId));
    LearningPlan plan = milestone.getPlan();
    plan.removeMilestone(milestone);
    learningMilestoneRepository.delete(milestone);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningMilestoneResponse get(UUID ownerId, UUID planId, UUID milestoneId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(milestoneId, "milestoneId must not be null");
    return learningMapper.toMilestoneResponse(
        requireOwnedMilestoneWithTopics(ownerId, planId, milestoneId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<LearningMilestoneResponse> list(UUID ownerId, UUID planId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    requireOwnedPlan(ownerId, planId);
    return learningMilestoneRepository
        .findByPlanIdAndOwnerIdOrderBySortOrderAsc(planId, ownerId)
        .stream()
        .map(learningMapper::toMilestoneResponse)
        .toList();
  }

  private LearningPlan requireOwnedPlan(UUID ownerId, UUID planId) {
    return learningPlanRepository
        .findByIdAndOwnerId(planId, ownerId)
        .orElseThrow(() -> new LearningPlanNotFoundException(planId));
  }

  private LearningMilestone requireOwnedMilestoneWithTopics(
      UUID ownerId, UUID planId, UUID milestoneId) {
    return learningMilestoneRepository
        .findWithTopicsByIdAndPlanIdAndOwnerId(milestoneId, planId, ownerId)
        .orElseThrow(() -> new LearningMilestoneNotFoundException(milestoneId));
  }
}
