package com.acos.career.repository;

import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.JobApplication;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link JobApplication}. */
public interface JobApplicationRepository
    extends JpaRepository<JobApplication, UUID>, JpaSpecificationExecutor<JobApplication> {

  @EntityGraph(attributePaths = {"company", "recruiter"})
  @Override
  Page<JobApplication> findAll(Specification<JobApplication> spec, Pageable pageable);

  /**
   * Finds a job application by id and owner.
   *
   * @param id application id
   * @param ownerId owner id
   * @return matching application, if present
   */
  Optional<JobApplication> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Finds a job application by id and owner with company and recruiter loaded.
   *
   * @param id application id
   * @param ownerId owner id
   * @return matching application, if present
   */
  @EntityGraph(attributePaths = {"company", "recruiter"})
  @Query(
      """
      select a from JobApplication a
      where a.id = :id and a.ownerId = :ownerId
      """)
  Optional<JobApplication> findWithDetailsByIdAndOwnerId(
      @Param("id") UUID id, @Param("ownerId") UUID ownerId);

  /**
   * Lists job applications for an owner with company and recruiter loaded, filtered by archival
   * state.
   *
   * @param ownerId owner id
   * @param archived {@code true} to list archived applications, {@code false} to list active ones
   * @param pageable paging
   * @return page of applications
   */
  @EntityGraph(attributePaths = {"company", "recruiter"})
  Page<JobApplication> findByOwnerIdAndArchived(UUID ownerId, boolean archived, Pageable pageable);

  /**
   * Reports whether any job application references the given company.
   *
   * @param companyId company id
   * @return {@code true} when at least one application references the company
   */
  boolean existsByCompanyId(UUID companyId);

  /**
   * Counts non-archived job applications for an owner.
   *
   * @param ownerId owner id
   * @return application count
   */
  long countByOwnerIdAndArchivedFalse(UUID ownerId);

  /**
   * Counts non-archived job applications for an owner with a given status.
   *
   * @param ownerId owner id
   * @param status application status
   * @return matching application count
   */
  long countByOwnerIdAndArchivedFalseAndStatus(UUID ownerId, ApplicationStatus status);

  /**
   * Counts non-archived job applications for an owner grouped by status.
   *
   * @param ownerId owner id
   * @return rows of {@code [ApplicationStatus, Long]}
   */
  @Query(
      """
      select a.status, count(a) from JobApplication a
      where a.ownerId = :ownerId and a.archived = false
      group by a.status
      """)
  List<Object[]> countGroupedByStatusForOwner(@Param("ownerId") UUID ownerId);
}
