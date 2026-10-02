package com.acos.career.service;

import com.acos.career.dto.RecruiterRequest;
import com.acos.career.dto.RecruiterResponse;
import java.util.List;
import java.util.UUID;

/** Application service for recruiter use-cases. */
public interface RecruiterService {

  /**
   * Creates a recruiter for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created recruiter
   */
  RecruiterResponse create(UUID ownerId, RecruiterRequest request);

  /**
   * Updates a recruiter owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param recruiterId recruiter id
   * @param request update payload
   * @return updated recruiter
   */
  RecruiterResponse update(UUID ownerId, UUID recruiterId, RecruiterRequest request);

  /**
   * Hard-deletes a recruiter owned by the authenticated user. Referencing job applications have
   * their recruiter reference cleared automatically at the database level.
   *
   * @param ownerId owning user id
   * @param recruiterId recruiter id
   */
  void delete(UUID ownerId, UUID recruiterId);

  /**
   * Returns a recruiter owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param recruiterId recruiter id
   * @return recruiter details
   */
  RecruiterResponse get(UUID ownerId, UUID recruiterId);

  /**
   * Lists recruiters for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return recruiters ordered by full name
   */
  List<RecruiterResponse> list(UUID ownerId);
}
