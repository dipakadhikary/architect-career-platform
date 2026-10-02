package com.acos.portfolio.service;

import com.acos.portfolio.dto.PortfolioProjectPageResponse;
import com.acos.portfolio.dto.PortfolioProjectRequest;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

/** Application service for portfolio project use-cases. */
public interface PortfolioProjectService {

  /**
   * Creates a portfolio project for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created project
   */
  PortfolioProjectResponse create(UUID ownerId, PortfolioProjectRequest request);

  /**
   * Updates a portfolio project owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param projectId project id
   * @param request update payload
   * @return updated project
   */
  PortfolioProjectResponse update(UUID ownerId, UUID projectId, PortfolioProjectRequest request);

  /**
   * Deletes a portfolio project owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param projectId project id
   */
  void delete(UUID ownerId, UUID projectId);

  /**
   * Returns a portfolio project owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param projectId project id
   * @return project details
   */
  PortfolioProjectResponse get(UUID ownerId, UUID projectId);

  /**
   * Lists portfolio projects for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param pageable paging and sorting
   * @return page of projects
   */
  PortfolioProjectPageResponse list(UUID ownerId, Pageable pageable);

  /**
   * Searches portfolio projects by title or summary for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param query search text
   * @param pageable paging and sorting
   * @return page of matching projects
   */
  PortfolioProjectPageResponse search(UUID ownerId, String query, Pageable pageable);
}
