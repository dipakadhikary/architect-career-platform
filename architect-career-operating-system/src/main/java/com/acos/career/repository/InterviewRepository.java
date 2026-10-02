package com.acos.career.repository;

import com.acos.career.entity.Interview;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link Interview}. */
public interface InterviewRepository extends JpaRepository<Interview, UUID> {

  /**
   * Finds an interview by id within an application owned by the given user.
   *
   * @param id interview id
   * @param applicationId application id
   * @param ownerId owner id
   * @return matching interview, if present
   */
  @Query(
      """
      select i from Interview i
      where i.id = :id
        and i.application.id = :applicationId
        and i.application.ownerId = :ownerId
        and i.archived = false
      """)
  Optional<Interview> findByIdAndApplicationIdAndOwner(
      @Param("id") UUID id,
      @Param("applicationId") UUID applicationId,
      @Param("ownerId") UUID ownerId);

  /**
   * Lists non-archived interviews for an application owned by the given user.
   *
   * @param applicationId application id
   * @param ownerId owner id
   * @return interviews ordered by interview date
   */
  @Query(
      """
      select i from Interview i
      where i.application.id = :applicationId
        and i.application.ownerId = :ownerId
        and i.archived = false
      order by i.interviewDate asc
      """)
  List<Interview> findByApplicationIdAndOwner(
      @Param("applicationId") UUID applicationId, @Param("ownerId") UUID ownerId);

  /**
   * Counts upcoming scheduled interviews for non-archived applications owned by the given user.
   *
   * @param ownerId owner id
   * @param now reference instant
   * @return upcoming interview count
   */
  @Query(
      """
      select count(i) from Interview i
      where i.application.ownerId = :ownerId
        and i.application.archived = false
        and i.archived = false
        and i.interviewDate >= :now
        and i.status = com.acos.career.entity.InterviewStatus.SCHEDULED
      """)
  long countUpcomingByOwnerId(@Param("ownerId") UUID ownerId, @Param("now") Instant now);

  /**
   * Counts scheduled interviews for non-archived applications owned by the given user.
   *
   * @param ownerId owner id
   * @return scheduled interview count
   */
  @Query(
      """
      select count(i) from Interview i
      where i.application.ownerId = :ownerId
        and i.application.archived = false
        and i.archived = false
        and i.status = com.acos.career.entity.InterviewStatus.SCHEDULED
      """)
  long countScheduledByOwnerId(@Param("ownerId") UUID ownerId);

  /**
   * Computes the average interview rating for non-archived interviews owned by the given user.
   *
   * @param ownerId owner id
   * @return average rating, or {@code null} when no rated interviews exist
   */
  @Query(
      """
      select avg(i.rating) from Interview i
      where i.application.ownerId = :ownerId
        and i.application.archived = false
        and i.archived = false
        and i.rating is not null
      """)
  Double averageRatingByOwnerId(@Param("ownerId") UUID ownerId);
}
