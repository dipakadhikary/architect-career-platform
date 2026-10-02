package com.acos.career.service;

import com.acos.career.dto.ApplicationStatusHistoryResponse;
import com.acos.career.dto.ApplicationTimelineResponse;
import com.acos.career.dto.JobApplicationPageResponse;
import com.acos.career.dto.JobApplicationRequest;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.JobApplicationSearchCriteria;
import com.acos.career.dto.StatusTransitionRequest;
import com.acos.career.entity.ApplicationStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/** Application service for job application use-cases, including the status lifecycle. */
public interface JobApplicationService {

  /**
   * Creates a job application for the authenticated owner, always starting in {@code DRAFT}.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created application
   */
  JobApplicationResponse create(UUID ownerId, JobApplicationRequest request);

  /**
   * Updates the mutable fields of a non-archived job application owned by the authenticated user.
   * Status is not affected; use {@link #transitionStatus} instead.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param request update payload
   * @return updated application
   */
  JobApplicationResponse update(UUID ownerId, UUID applicationId, JobApplicationRequest request);

  /**
   * Soft-archives a job application owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   */
  void archive(UUID ownerId, UUID applicationId);

  /**
   * Returns a job application with company and recruiter details.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @return application details
   */
  JobApplicationResponse get(UUID ownerId, UUID applicationId);

  /**
   * Lists job applications for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param pageable paging and sorting
   * @param archived {@code true} to list only archived applications, {@code false} for active
   *     applications
   * @return page of applications
   */
  JobApplicationPageResponse list(UUID ownerId, Pageable pageable, boolean archived);

  /**
   * Searches non-archived job applications for the authenticated owner using optional filters.
   *
   * @param ownerId owning user id
   * @param criteria optional search filters
   * @param pageable paging and sorting
   * @return page of matching applications
   */
  JobApplicationPageResponse search(
      UUID ownerId, JobApplicationSearchCriteria criteria, Pageable pageable);

  /**
   * Transitions the status of a job application, validating the transition, recording history,
   * auditing, and publishing domain events.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param request transition payload
   * @return updated application
   */
  JobApplicationResponse transitionStatus(
      UUID ownerId, UUID applicationId, StatusTransitionRequest request);

  /**
   * Attempts to synchronize the application status with an externally driven target status (e.g.
   * from an offer decision), silently skipping when the transition is not valid from the current
   * status.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param targetStatus desired status
   * @param actorId user id who triggered the synchronization
   */
  void trySyncStatus(
      UUID ownerId, UUID applicationId, ApplicationStatus targetStatus, UUID actorId);

  /**
   * Returns the ordered status transition history for a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @return status history ordered chronologically
   */
  List<ApplicationStatusHistoryResponse> getHistory(UUID ownerId, UUID applicationId);

  /**
   * Returns the milestone timeline for a job application, for visualization.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @return ordered timeline milestones
   */
  ApplicationTimelineResponse getTimeline(UUID ownerId, UUID applicationId);
}
