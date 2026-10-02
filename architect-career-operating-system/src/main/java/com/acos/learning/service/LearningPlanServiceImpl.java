package com.acos.learning.service;

import com.acos.learning.dto.LearningPlanPageResponse;
import com.acos.learning.dto.LearningPlanRequest;
import com.acos.learning.dto.LearningPlanResponse;
import com.acos.learning.dto.LearningPlanSummaryResponse;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.event.LearningDomainEventPublisher;
import com.acos.learning.event.LearningPlanCompletedEvent;
import com.acos.learning.exception.LearningPlanNotFoundException;
import com.acos.learning.mapper.LearningMapper;
import com.acos.learning.repository.LearningPlanRepository;
import com.acos.learning.validator.LearningValidator;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link LearningPlanService} implementation. */
@Service
@Transactional
public class LearningPlanServiceImpl implements LearningPlanService {

  private final LearningPlanRepository learningPlanRepository;
  private final LearningMapper learningMapper;
  private final LearningValidator learningValidator;
  private final LearningDomainEventPublisher learningDomainEventPublisher;

  /**
   * Creates the learning plan service.
   *
   * @param learningPlanRepository plan repository
   * @param learningMapper mapper
   * @param learningValidator validator
   * @param learningDomainEventPublisher domain event publisher
   */
  public LearningPlanServiceImpl(
      LearningPlanRepository learningPlanRepository,
      LearningMapper learningMapper,
      LearningValidator learningValidator,
      LearningDomainEventPublisher learningDomainEventPublisher) {
    this.learningPlanRepository =
        Objects.requireNonNull(learningPlanRepository, "learningPlanRepository must not be null");
    this.learningMapper = Objects.requireNonNull(learningMapper, "learningMapper must not be null");
    this.learningValidator =
        Objects.requireNonNull(learningValidator, "learningValidator must not be null");
    this.learningDomainEventPublisher =
        Objects.requireNonNull(
            learningDomainEventPublisher, "learningDomainEventPublisher must not be null");
  }

  @Override
  public LearningPlanResponse create(UUID ownerId, LearningPlanRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningPlan plan =
        new LearningPlan(
            ownerId,
            request.title().trim(),
            learningValidator.normalizeDescription(request.description()),
            request.status(),
            request.targetDate());
    LearningPlan saved = learningPlanRepository.save(plan);
    if (saved.getStatus() == LearningPlanStatus.COMPLETED) {
      learningDomainEventPublisher.publish(
          new LearningPlanCompletedEvent(saved.getId(), saved.getOwnerId(), Instant.now()));
    }
    return learningMapper.toPlanResponse(saved);
  }

  @Override
  public LearningPlanResponse update(UUID ownerId, UUID planId, LearningPlanRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    LearningPlan plan = requireOwnedPlanWithDetails(ownerId, planId);
    LearningPlanStatus previousStatus = plan.getStatus();
    plan.setTitle(request.title().trim());
    plan.setDescription(learningValidator.normalizeDescription(request.description()));
    plan.setStatus(request.status());
    plan.setTargetDate(request.targetDate());
    if (request.status() == LearningPlanStatus.COMPLETED
        && previousStatus != LearningPlanStatus.COMPLETED) {
      learningDomainEventPublisher.publish(
          new LearningPlanCompletedEvent(plan.getId(), plan.getOwnerId(), Instant.now()));
    }
    return learningMapper.toPlanResponse(plan);
  }

  @Override
  public void delete(UUID ownerId, UUID planId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    LearningPlan plan =
        learningPlanRepository
            .findByIdAndOwnerId(planId, ownerId)
            .orElseThrow(() -> new LearningPlanNotFoundException(planId));
    learningPlanRepository.delete(plan);
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPlanResponse get(UUID ownerId, UUID planId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(planId, "planId must not be null");
    return learningMapper.toPlanResponse(requireOwnedPlanWithDetails(ownerId, planId));
  }

  @Override
  @Transactional(readOnly = true)
  public LearningPlanPageResponse list(UUID ownerId, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    learningValidator.validatePageable(pageable);
    Page<LearningPlan> page = learningPlanRepository.findByOwnerId(ownerId, pageable);
    List<LearningPlanSummaryResponse> content =
        page.getContent().stream()
            .map(
                plan -> {
                  long total = learningPlanRepository.countTopicsByPlanId(plan.getId());
                  long completed =
                      learningPlanRepository.countCompletedTopicsByPlanId(plan.getId());
                  return learningMapper.toPlanSummary(plan, total, completed);
                })
            .toList();
    return new LearningPlanPageResponse(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  private LearningPlan requireOwnedPlanWithDetails(UUID ownerId, UUID planId) {
    return learningPlanRepository
        .findWithDetailsByIdAndOwnerId(planId, ownerId)
        .orElseThrow(() -> new LearningPlanNotFoundException(planId));
  }
}
