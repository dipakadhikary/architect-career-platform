package com.acos.career.repository;

import com.acos.career.entity.Offer;
import com.acos.career.entity.OfferStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link Offer}. */
public interface OfferRepository extends JpaRepository<Offer, UUID> {

  /**
   * Finds an offer by id within an application owned by the given user.
   *
   * @param id offer id
   * @param applicationId application id
   * @param ownerId owner id
   * @return matching offer, if present
   */
  @Query(
      """
      select o from Offer o
      where o.id = :id
        and o.application.id = :applicationId
        and o.application.ownerId = :ownerId
        and o.archived = false
      """)
  Optional<Offer> findByIdAndApplicationIdAndOwner(
      @Param("id") UUID id,
      @Param("applicationId") UUID applicationId,
      @Param("ownerId") UUID ownerId);

  /**
   * Lists non-archived offers for an application owned by the given user.
   *
   * @param applicationId application id
   * @param ownerId owner id
   * @return offers ordered by creation time descending
   */
  @Query(
      """
      select o from Offer o
      where o.application.id = :applicationId
        and o.application.ownerId = :ownerId
        and o.archived = false
      order by o.createdAt desc
      """)
  List<Offer> findByApplicationIdAndOwner(
      @Param("applicationId") UUID applicationId, @Param("ownerId") UUID ownerId);

  /**
   * Counts offers for non-archived applications owned by the given user with a given status.
   *
   * @param ownerId owner id
   * @param offerStatus offer status
   * @return matching offer count
   */
  @Query(
      """
      select count(o) from Offer o
      where o.application.ownerId = :ownerId
        and o.application.archived = false
        and o.archived = false
        and o.offerStatus = :offerStatus
      """)
  long countByOwnerIdAndOfferStatus(
      @Param("ownerId") UUID ownerId, @Param("offerStatus") OfferStatus offerStatus);

  /**
   * Counts all non-archived offers for non-archived applications owned by the given user.
   *
   * @param ownerId owner id
   * @return offer count
   */
  @Query(
      """
      select count(o) from Offer o
      where o.application.ownerId = :ownerId
        and o.application.archived = false
        and o.archived = false
      """)
  long countByOwnerId(@Param("ownerId") UUID ownerId);

  /**
   * Reports whether a non-archived pending offer already exists for the given application.
   *
   * @param applicationId application id
   * @return {@code true} when an active pending offer exists
   */
  @Query(
      """
      select case when count(o) > 0 then true else false end from Offer o
      where o.application.id = :applicationId
        and o.archived = false
        and o.offerStatus = com.acos.career.entity.OfferStatus.PENDING
      """)
  boolean existsActiveByApplicationId(@Param("applicationId") UUID applicationId);
}
