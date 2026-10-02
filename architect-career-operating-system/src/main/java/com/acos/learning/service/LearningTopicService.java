package com.acos.learning.service;

import com.acos.learning.dto.LearningTopicRequest;
import com.acos.learning.dto.LearningTopicResponse;
import com.acos.learning.dto.TopicStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

/** Application service for learning topic use-cases. */
public interface LearningTopicService {

  /**
   * Creates a topic under a milestone owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @param request create payload
   * @return created topic
   */
  LearningTopicResponse create(
      UUID ownerId, UUID planId, UUID milestoneId, LearningTopicRequest request);

  /**
   * Updates a topic under a milestone owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   * @param request update payload
   * @return updated topic
   */
  LearningTopicResponse update(
      UUID ownerId, UUID planId, UUID milestoneId, UUID topicId, LearningTopicRequest request);

  /**
   * Deletes a topic under a milestone owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   */
  void delete(UUID ownerId, UUID planId, UUID milestoneId, UUID topicId);

  /**
   * Updates only the status of a topic.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @param topicId topic id
   * @param request status payload
   * @return updated topic
   */
  LearningTopicResponse updateStatus(
      UUID ownerId, UUID planId, UUID milestoneId, UUID topicId, TopicStatusUpdateRequest request);

  /**
   * Lists topics for a milestone owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param planId plan id
   * @param milestoneId milestone id
   * @return topics ordered by sort order
   */
  List<LearningTopicResponse> list(UUID ownerId, UUID planId, UUID milestoneId);
}
