package com.acos.portfolio.service;

import com.acos.portfolio.dto.SkillRequest;
import com.acos.portfolio.dto.SkillResponse;
import java.util.List;
import java.util.UUID;

/** Application service for skill use-cases. */
public interface SkillService {

  /**
   * Creates a skill for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created skill
   */
  SkillResponse create(UUID ownerId, SkillRequest request);

  /**
   * Updates a skill owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param skillId skill id
   * @param request update payload
   * @return updated skill
   */
  SkillResponse update(UUID ownerId, UUID skillId, SkillRequest request);

  /**
   * Deletes a skill owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param skillId skill id
   */
  void delete(UUID ownerId, UUID skillId);

  /**
   * Returns a skill owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param skillId skill id
   * @return skill details
   */
  SkillResponse get(UUID ownerId, UUID skillId);

  /**
   * Lists skills for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return skills ordered by name
   */
  List<SkillResponse> list(UUID ownerId);
}
