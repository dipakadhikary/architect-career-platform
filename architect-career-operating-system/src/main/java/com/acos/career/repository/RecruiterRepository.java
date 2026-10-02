package com.acos.career.repository;

import com.acos.career.entity.Recruiter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link Recruiter}. */
public interface RecruiterRepository extends JpaRepository<Recruiter, UUID> {

  /**
   * Finds a non-archived recruiter by id and owner.
   *
   * @param id recruiter id
   * @param ownerId owner id
   * @return matching recruiter, if present
   */
  Optional<Recruiter> findByIdAndOwnerIdAndArchivedFalse(UUID id, UUID ownerId);

  /**
   * Lists non-archived recruiters for an owner ordered by full name.
   *
   * @param ownerId owner id
   * @return recruiters
   */
  List<Recruiter> findByOwnerIdAndArchivedFalseOrderByFullNameAsc(UUID ownerId);

  /**
   * Counts non-archived recruiters for an owner.
   *
   * @param ownerId owner id
   * @return recruiter count
   */
  long countByOwnerIdAndArchivedFalse(UUID ownerId);

  /**
   * Reports whether another active recruiter for the owner already uses the given email.
   *
   * @param ownerId owner id
   * @param email email address
   * @return {@code true} when a conflicting recruiter exists
   */
  @Query(
      """
      select case when count(r) > 0 then true else false end from Recruiter r
      where r.ownerId = :ownerId
        and r.archived = false
        and lower(r.email) = lower(:email)
      """)
  boolean existsActiveByOwnerIdAndEmailIgnoreCase(
      @Param("ownerId") UUID ownerId, @Param("email") String email);

  /**
   * Reports whether another active recruiter for the owner already uses the given email, excluding
   * one id.
   *
   * @param ownerId owner id
   * @param email email address
   * @param id recruiter id to exclude
   * @return {@code true} when a conflicting recruiter exists
   */
  @Query(
      """
      select case when count(r) > 0 then true else false end from Recruiter r
      where r.ownerId = :ownerId
        and r.archived = false
        and r.id <> :id
        and lower(r.email) = lower(:email)
      """)
  boolean existsActiveByOwnerIdAndEmailIgnoreCaseAndIdNot(
      @Param("ownerId") UUID ownerId, @Param("email") String email, @Param("id") UUID id);
}
