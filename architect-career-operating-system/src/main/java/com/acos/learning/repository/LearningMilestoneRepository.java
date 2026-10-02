package com.acos.learning.repository;

import com.acos.learning.entity.LearningMilestone;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link LearningMilestone}. */
public interface LearningMilestoneRepository extends JpaRepository<LearningMilestone, UUID> {

  /**
   * Finds a milestone by id within a plan owned by the given user.
   *
   * @param id milestone id
   * @param planId plan id
   * @param ownerId owner id
   * @return matching milestone, if present
   */
  @Query(
      """
      select m from LearningMilestone m
      where m.id = :id
        and m.plan.id = :planId
        and m.plan.ownerId = :ownerId
      """)
  Optional<LearningMilestone> findByIdAndPlanIdAndOwnerId(
      @Param("id") UUID id, @Param("planId") UUID planId, @Param("ownerId") UUID ownerId);

  /**
   * Finds a milestone with topics loaded.
   *
   * @param id milestone id
   * @param planId plan id
   * @param ownerId owner id
   * @return matching milestone, if present
   */
  @EntityGraph(attributePaths = {"topics"})
  @Query(
      """
      select m from LearningMilestone m
      where m.id = :id
        and m.plan.id = :planId
        and m.plan.ownerId = :ownerId
      """)
  Optional<LearningMilestone> findWithTopicsByIdAndPlanIdAndOwnerId(
      @Param("id") UUID id, @Param("planId") UUID planId, @Param("ownerId") UUID ownerId);

  /**
   * Lists milestones for a plan owned by the given user.
   *
   * @param planId plan id
   * @param ownerId owner id
   * @return milestones ordered by sort order
   */
  @EntityGraph(attributePaths = {"topics"})
  @Query(
      """
      select m from LearningMilestone m
      where m.plan.id = :planId
        and m.plan.ownerId = :ownerId
      order by m.sortOrder asc
      """)
  List<LearningMilestone> findByPlanIdAndOwnerIdOrderBySortOrderAsc(
      @Param("planId") UUID planId, @Param("ownerId") UUID ownerId);

  /**
   * Counts milestones for a plan.
   *
   * @param planId plan id
   * @return milestone count
   */
  long countByPlanId(UUID planId);

  /**
   * Returns the maximum sort order for milestones in a plan.
   *
   * @param planId plan id
   * @return max sort order, or empty when none exist
   */
  @Query("select max(m.sortOrder) from LearningMilestone m where m.plan.id = :planId")
  Optional<Integer> findMaxSortOrderByPlanId(@Param("planId") UUID planId);
}
