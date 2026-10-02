package com.acos.portfolio.service;

import com.acos.portfolio.dto.TechnologyRequest;
import com.acos.portfolio.dto.TechnologyResponse;
import java.util.List;
import java.util.UUID;

/** Application service for technology catalog use-cases. */
public interface TechnologyService {

  /**
   * Creates a technology for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created technology
   */
  TechnologyResponse create(UUID ownerId, TechnologyRequest request);

  /**
   * Updates a technology owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param technologyId technology id
   * @param request update payload
   * @return updated technology
   */
  TechnologyResponse update(UUID ownerId, UUID technologyId, TechnologyRequest request);

  /**
   * Deletes a technology owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param technologyId technology id
   */
  void delete(UUID ownerId, UUID technologyId);

  /**
   * Returns a technology owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param technologyId technology id
   * @return technology details
   */
  TechnologyResponse get(UUID ownerId, UUID technologyId);

  /**
   * Lists technologies for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return technologies ordered by name
   */
  List<TechnologyResponse> list(UUID ownerId);
}
