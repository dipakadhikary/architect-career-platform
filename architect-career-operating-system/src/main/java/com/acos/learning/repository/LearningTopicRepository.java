package com.acos.learning.repository;

import com.acos.learning.entity.LearningTopic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link LearningTopic}. */
public interface LearningTopicRepository extends JpaRepository<LearningTopic, UUID> {

  /**
   * Finds a topic by id within a milestone and plan owned by the given user.
   *
   * @param id topic id
   * @param milestoneId milestone id
   * @param planId plan id
   * @param ownerId owner id
   * @return matching topic, if present
   */
  @Query(
      """
      select t from LearningTopic t
      where t.id = :id
        and t.milestone.id = :milestoneId
        and t.milestone.plan.id = :planId
        and t.milestone.plan.ownerId = :ownerId
      """)
  Optional<LearningTopic> findByIdAndMilestoneIdAndPlanIdAndOwnerId(
      @Param("id") UUID id,
      @Param("milestoneId") UUID milestoneId,
      @Param("planId") UUID planId,
      @Param("ownerId") UUID ownerId);

  /**
   * Lists topics for a milestone owned by the given user.
   *
   * @param milestoneId milestone id
   * @param planId plan id
   * @param ownerId owner id
   * @return topics ordered by sort order
   */
  @Query(
      """
      select t from LearningTopic t
      where t.milestone.id = :milestoneId
        and t.milestone.plan.id = :planId
        and t.milestone.plan.ownerId = :ownerId
      order by t.sortOrder asc
      """)
  List<LearningTopic> findByMilestoneIdAndPlanIdAndOwnerIdOrderBySortOrderAsc(
      @Param("milestoneId") UUID milestoneId,
      @Param("planId") UUID planId,
      @Param("ownerId") UUID ownerId);

  /**
   * Counts topics for a milestone.
   *
   * @param milestoneId milestone id
   * @return topic count
   */
  long countByMilestoneId(UUID milestoneId);

  /**
   * Returns the maximum sort order for topics in a milestone.
   *
   * @param milestoneId milestone id
   * @return max sort order, or empty when none exist
   */
  @Query("select max(t.sortOrder) from LearningTopic t where t.milestone.id = :milestoneId")
  Optional<Integer> findMaxSortOrderByMilestoneId(@Param("milestoneId") UUID milestoneId);
}
