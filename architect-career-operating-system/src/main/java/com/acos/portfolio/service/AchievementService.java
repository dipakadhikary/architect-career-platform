package com.acos.portfolio.service;

import com.acos.portfolio.dto.AchievementRequest;
import com.acos.portfolio.dto.AchievementResponse;
import java.util.List;
import java.util.UUID;

/** Application service for achievement use-cases. */
public interface AchievementService {

  /**
   * Creates an achievement for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created achievement
   */
  AchievementResponse create(UUID ownerId, AchievementRequest request);

  /**
   * Updates an achievement owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param achievementId achievement id
   * @param request update payload
   * @return updated achievement
   */
  AchievementResponse update(UUID ownerId, UUID achievementId, AchievementRequest request);

  /**
   * Deletes an achievement owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param achievementId achievement id
   */
  void delete(UUID ownerId, UUID achievementId);

  /**
   * Returns an achievement owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param achievementId achievement id
   * @return achievement details
   */
  AchievementResponse get(UUID ownerId, UUID achievementId);

  /**
   * Lists achievements for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return achievements ordered by achieved date descending
   */
  List<AchievementResponse> list(UUID ownerId);
}
