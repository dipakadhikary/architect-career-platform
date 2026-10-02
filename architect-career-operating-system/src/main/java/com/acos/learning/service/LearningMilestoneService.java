package com.acos.learning.service;

import com.acos.learning.dto.LearningMilestoneRequest;
import com.acos.learning.dto.LearningMilestoneResponse;
import java.util.List;
import java.util.UUID;

/** Application service for learning milestone use-cases. */
public interface LearningMilestoneService {

  /**
   * Creates a milestone under a plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param request create payload
   * @return created milestone
   */
  LearningMilestoneResponse create(UUID ownerId, UUID planId, LearningMilestoneRequest request);

  /**
   * Updates a milestone under a plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @param request update payload
   * @return updated milestone
   */
  LearningMilestoneResponse update(
      UUID ownerId, UUID planId, UUID milestoneId, LearningMilestoneRequest request);

  /**
   * Deletes a milestone under a plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   */
  void delete(UUID ownerId, UUID planId, UUID milestoneId);

  /**
   * Returns a milestone with topics and progress.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @return milestone details
   */
  LearningMilestoneResponse get(UUID ownerId, UUID planId, UUID milestoneId);

  /**
   * Lists milestones for a plan owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @return milestones ordered by sort order
   */
  List<LearningMilestoneResponse> list(UUID ownerId, UUID planId);
}
