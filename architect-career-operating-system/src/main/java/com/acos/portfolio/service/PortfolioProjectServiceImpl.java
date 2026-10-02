package com.acos.portfolio.service;

import com.acos.portfolio.dto.PortfolioProjectPageResponse;
import com.acos.portfolio.dto.PortfolioProjectRequest;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import com.acos.portfolio.entity.PortfolioProject;
import com.acos.portfolio.entity.Technology;
import com.acos.portfolio.event.PortfolioDomainEventPublisher;
import com.acos.portfolio.event.PortfolioUpdatedEvent;
import com.acos.portfolio.exception.PortfolioProjectNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.PortfolioProjectRepository;
import com.acos.portfolio.repository.TechnologyRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link PortfolioProjectService} implementation. */
@Service
@Transactional
public class PortfolioProjectServiceImpl implements PortfolioProjectService {

  private final PortfolioProjectRepository portfolioProjectRepository;
  private final TechnologyRepository technologyRepository;
  private final PortfolioMapper portfolioMapper;
  private final PortfolioValidator portfolioValidator;
  private final PortfolioDomainEventPublisher portfolioDomainEventPublisher;

  /**
   * Creates the portfolio project service.
   *
   * @param portfolioProjectRepository project repository
   * @param technologyRepository technology repository
   * @param portfolioMapper mapper
   * @param portfolioValidator validator
   * @param portfolioDomainEventPublisher domain event publisher
   */
  public PortfolioProjectServiceImpl(
      PortfolioProjectRepository portfolioProjectRepository,
      TechnologyRepository technologyRepository,
      PortfolioMapper portfolioMapper,
      PortfolioValidator portfolioValidator,
      PortfolioDomainEventPublisher portfolioDomainEventPublisher) {
    this.portfolioProjectRepository =
        Objects.requireNonNull(
            portfolioProjectRepository, "portfolioProjectRepository must not be null");
    this.technologyRepository =
        Objects.requireNonNull(technologyRepository, "technologyRepository must not be null");
    this.portfolioMapper =
        Objects.requireNonNull(portfolioMapper, "portfolioMapper must not be null");
    this.portfolioValidator =
        Objects.requireNonNull(portfolioValidator, "portfolioValidator must not be null");
    this.portfolioDomainEventPublisher =
        Objects.requireNonNull(
            portfolioDomainEventPublisher, "portfolioDomainEventPublisher must not be null");
  }

  @Override
  public PortfolioProjectResponse create(UUID ownerId, PortfolioProjectRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    portfolioValidator.validateDescription(request.description());
    portfolioValidator.validateDateRange(request.startDate(), request.endDate());
    List<String> technologyNames =
        portfolioValidator.normalizeTechnologyNames(request.technologyNames());

    PortfolioProject project =
        new PortfolioProject(
            ownerId,
            request.title().trim(),
            request.summary().trim(),
            request.description(),
            request.status());
    project.setRepositoryUrl(portfolioValidator.normalizeOptionalText(request.repositoryUrl()));
    project.setLiveUrl(portfolioValidator.normalizeOptionalText(request.liveUrl()));
    project.setStartDate(request.startDate());
    project.setEndDate(request.endDate());
    project.replaceTechnologies(resolveTechnologies(ownerId, technologyNames));

    PortfolioProject saved = portfolioProjectRepository.save(project);
    portfolioDomainEventPublisher.publish(
        new PortfolioUpdatedEvent(
            saved.getId(), saved.getOwnerId(), saved.getTitle(), Instant.now()));
    return portfolioMapper.toProjectResponse(saved);
  }

  @Override
  public PortfolioProjectResponse update(
      UUID ownerId, UUID projectId, PortfolioProjectRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(projectId, "projectId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    PortfolioProject project = requireOwnedProject(ownerId, projectId);
    portfolioValidator.validateDescription(request.description());
    portfolioValidator.validateDateRange(request.startDate(), request.endDate());

    project.setTitle(request.title().trim());
    project.setSummary(request.summary().trim());
    project.setDescription(request.description());
    project.setRepositoryUrl(portfolioValidator.normalizeOptionalText(request.repositoryUrl()));
    project.setLiveUrl(portfolioValidator.normalizeOptionalText(request.liveUrl()));
    project.setStatus(request.status());
    project.setStartDate(request.startDate());
    project.setEndDate(request.endDate());

    if (request.technologyNames() != null) {
      List<String> technologyNames =
          portfolioValidator.normalizeTechnologyNames(request.technologyNames());
      project.replaceTechnologies(resolveTechnologies(ownerId, technologyNames));
    }

    portfolioDomainEventPublisher.publish(
        new PortfolioUpdatedEvent(
            project.getId(), project.getOwnerId(), project.getTitle(), Instant.now()));
    return portfolioMapper.toProjectResponse(project);
  }

  @Override
  public void delete(UUID ownerId, UUID projectId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(projectId, "projectId must not be null");
    PortfolioProject project = requireOwnedProject(ownerId, projectId);
    portfolioProjectRepository.delete(project);
  }

  @Override
  @Transactional(readOnly = true)
  public PortfolioProjectResponse get(UUID ownerId, UUID projectId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(projectId, "projectId must not be null");
    return portfolioMapper.toProjectResponse(requireOwnedProject(ownerId, projectId));
  }

  @Override
  @Transactional(readOnly = true)
  public PortfolioProjectPageResponse list(UUID ownerId, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    portfolioValidator.validatePageable(pageable);
    Page<PortfolioProject> page = portfolioProjectRepository.findByOwnerId(ownerId, pageable);
    return portfolioMapper.toPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PortfolioProjectPageResponse search(UUID ownerId, String query, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    portfolioValidator.validatePageable(pageable);
    String normalizedQuery = portfolioValidator.normalizeSearchQuery(query);
    Page<PortfolioProject> page =
        portfolioProjectRepository.searchByOwnerIdAndTitleOrSummary(
            ownerId, normalizedQuery, pageable);
    return portfolioMapper.toPageResponse(page);
  }

  private PortfolioProject requireOwnedProject(UUID ownerId, UUID projectId) {
    return portfolioProjectRepository
        .findByIdAndOwnerId(projectId, ownerId)
        .orElseThrow(() -> new PortfolioProjectNotFoundException(projectId));
  }

  private Set<Technology> resolveTechnologies(UUID ownerId, List<String> technologyNames) {
    if (technologyNames.isEmpty()) {
      return Set.of();
    }
    List<Technology> existing =
        technologyRepository.findByOwnerIdAndNameIn(ownerId, technologyNames);
    Map<String, Technology> byName =
        existing.stream().collect(Collectors.toMap(Technology::getName, Function.identity()));

    List<Technology> created =
        technologyNames.stream()
            .filter(name -> !byName.containsKey(name))
            .map(name -> new Technology(ownerId, name, null))
            .map(technologyRepository::save)
            .toList();
    created.forEach(technology -> byName.put(technology.getName(), technology));

    Set<Technology> resolved = new HashSet<>();
    for (String name : technologyNames) {
      resolved.add(byName.get(name));
    }
    return resolved;
  }
}
