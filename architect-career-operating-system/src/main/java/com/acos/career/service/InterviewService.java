package com.acos.career.service;

import com.acos.career.dto.InterviewRequest;
import com.acos.career.dto.InterviewResponse;
import java.util.List;
import java.util.UUID;

/** Application service for interview scheduling use-cases. */
public interface InterviewService {

  /**
   * Creates an interview under a job application, always starting as {@code SCHEDULED}.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param request create payload
   * @return created interview
   */
  InterviewResponse create(UUID ownerId, UUID applicationId, InterviewRequest request);

  /**
   * Updates an interview under a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param interviewId interview id
   * @param request update payload
   * @return updated interview
   */
  InterviewResponse update(
      UUID ownerId, UUID applicationId, UUID interviewId, InterviewRequest request);

  /**
   * Hard-deletes an interview under a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param interviewId interview id
   */
  void delete(UUID ownerId, UUID applicationId, UUID interviewId);

  /**
   * Returns an interview under a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param interviewId interview id
   * @return interview details
   */
  InterviewResponse get(UUID ownerId, UUID applicationId, UUID interviewId);

  /**
   * Lists interviews for a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @return interviews ordered by interview date
   */
  List<InterviewResponse> list(UUID ownerId, UUID applicationId);
}
