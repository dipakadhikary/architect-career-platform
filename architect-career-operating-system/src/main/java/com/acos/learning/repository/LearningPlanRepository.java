package com.acos.learning.repository;

import com.acos.learning.entity.LearningPlan;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link LearningPlan}. */
public interface LearningPlanRepository extends JpaRepository<LearningPlan, UUID> {

  /**
   * Finds a plan by id and owner without nested collections.
   *
   * @param id plan id
   * @param ownerId owner id
   * @return matching plan, if present
   */
  Optional<LearningPlan> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Finds a plan by id and owner with milestones and topics loaded.
   *
   * @param id plan id
   * @param ownerId owner id
   * @return matching plan, if present
   */
  @EntityGraph(attributePaths = {"milestones"})
  @Query("select p from LearningPlan p where p.id = :id and p.ownerId = :ownerId")
  Optional<LearningPlan> findWithDetailsByIdAndOwnerId(
      @Param("id") UUID id, @Param("ownerId") UUID ownerId);

  /**
   * Lists plans for an owner.
   *
   * @param ownerId owner id
   * @param pageable paging
   * @return page of plans
   */
  Page<LearningPlan> findByOwnerId(UUID ownerId, Pageable pageable);

  /**
   * Counts topics belonging to a plan.
   *
   * @param planId plan id
   * @return topic count
   */
  @Query(
      """
      select count(t) from LearningTopic t
      where t.milestone.plan.id = :planId
      """)
  long countTopicsByPlanId(@Param("planId") UUID planId);

  /**
   * Counts completed topics belonging to a plan.
   *
   * @param planId plan id
   * @return completed topic count
   */
  @Query(
      """
      select count(t) from LearningTopic t
      where t.milestone.plan.id = :planId
        and t.status = com.acos.learning.entity.TopicStatus.COMPLETED
      """)
  long countCompletedTopicsByPlanId(@Param("planId") UUID planId);
}
