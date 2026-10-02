package com.acos.portfolio.repository;

import com.acos.portfolio.entity.PortfolioProject;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Persistence operations for {@link PortfolioProject}. */
public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, UUID> {

  /**
   * Finds a project by id and owner with technologies loaded.
   *
   * @param id project id
   * @param ownerId owner id
   * @return matching project, if present
   */
  @EntityGraph(attributePaths = {"technologies"})
  Optional<PortfolioProject> findByIdAndOwnerId(UUID id, UUID ownerId);

  /**
   * Lists projects for an owner. Technologies are batch-loaded.
   *
   * @param ownerId owner id
   * @param pageable paging
   * @return page of projects
   */
  Page<PortfolioProject> findByOwnerId(UUID ownerId, Pageable pageable);

  /**
   * Searches projects by title or summary for an owner. Technologies are batch-loaded.
   *
   * @param ownerId owner id
   * @param query search text
   * @param pageable paging
   * @return page of matching projects
   */
  @Query(
      """
      select p from PortfolioProject p
      where p.ownerId = :ownerId
        and (lower(p.title) like lower(concat('%', :query, '%'))
          or lower(p.summary) like lower(concat('%', :query, '%')))
      """)
  Page<PortfolioProject> searchByOwnerIdAndTitleOrSummary(
      @Param("ownerId") UUID ownerId, @Param("query") String query, Pageable pageable);
}
