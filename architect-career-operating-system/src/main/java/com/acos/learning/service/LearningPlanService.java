package com.acos.learning.service;

import com.acos.learning.dto.LearningPlanPageResponse;
import com.acos.learning.dto.LearningPlanRequest;
import com.acos.learning.dto.LearningPlanResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/** Application service for learning plan use-cases. */
public interface LearningPlanService {

  /**
   * Creates a learning plan for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created plan
   */
  LearningPlanResponse create(UUID ownerId, LearningPlanRequest request);

  /**
   * Updates a learning plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param request update payload
   * @return updated plan
   */
  LearningPlanResponse update(UUID ownerId, UUID planId, LearningPlanRequest request);

  /**
   * Deletes a learning plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   */
  void delete(UUID ownerId, UUID planId);

  /**
   * Returns a learning plan with milestones, topics, and progress.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @return plan details
   */
  LearningPlanResponse get(UUID ownerId, UUID planId);

  /**
   * Lists learning plans for the authenticated owner with progress summaries.
   *
   * @param ownerId owning user id
   * @param pageable paging and sorting
   * @return page of plan summaries
   */
  LearningPlanPageResponse list(UUID ownerId, Pageable pageable);
}
