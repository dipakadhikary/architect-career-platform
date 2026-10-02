package com.acos.portfolio.repository;

import com.acos.portfolio.entity.Achievement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence operations for {@link Achievement}. */
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

  /**
   * Finds an achievement by id and owner.
   *
   * @param id achievement id
   * @param ownerId owner id
   * @return matching achievement, if present
   */
  Optional<Achievement> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists achievements for an owner ordered by date descending.
   *
   * @param ownerId owner id
   * @return achievements
   */
  List<Achievement> findByOwnerIdOrderByAchievedOnDesc(UUID ownerId);
}
