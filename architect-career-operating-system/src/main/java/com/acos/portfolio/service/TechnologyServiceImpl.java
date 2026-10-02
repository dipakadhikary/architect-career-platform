package com.acos.portfolio.service;

import com.acos.portfolio.dto.TechnologyRequest;
import com.acos.portfolio.dto.TechnologyResponse;
import com.acos.portfolio.entity.Technology;
import com.acos.portfolio.exception.DuplicatePortfolioNameException;
import com.acos.portfolio.exception.TechnologyNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.TechnologyRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link TechnologyService} implementation. */
@Service
@Transactional
public class TechnologyServiceImpl implements TechnologyService {

  private final TechnologyRepository technologyRepository;
  private final PortfolioMapper portfolioMapper;
  private final PortfolioValidator portfolioValidator;

  /**
   * Creates the technology service.
   *
   * @param technologyRepository technology repository
   * @param portfolioMapper mapper
   * @param portfolioValidator validator
   */
  public TechnologyServiceImpl(
      TechnologyRepository technologyRepository,
      PortfolioMapper portfolioMapper,
      PortfolioValidator portfolioValidator) {
    this.technologyRepository =
        Objects.requireNonNull(technologyRepository, "technologyRepository must not be null");
    this.portfolioMapper =
        Objects.requireNonNull(portfolioMapper, "portfolioMapper must not be null");
    this.portfolioValidator =
        Objects.requireNonNull(portfolioValidator, "portfolioValidator must not be null");
  }

  @Override
  public TechnologyResponse create(UUID ownerId, TechnologyRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    String name = request.name().trim();
    if (technologyRepository.existsByOwnerIdAndName(ownerId, name)) {
      throw new DuplicatePortfolioNameException("Technology", name);
    }

    Technology technology =
        new Technology(ownerId, name, portfolioValidator.normalizeOptionalText(request.category()));
    Technology saved = technologyRepository.save(technology);
    return portfolioMapper.toTechnologyResponse(saved);
  }

  @Override
  public TechnologyResponse update(UUID ownerId, UUID technologyId, TechnologyRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(technologyId, "technologyId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Technology technology = requireOwnedTechnology(ownerId, technologyId);
    String name = request.name().trim();
    if (technologyRepository.existsByOwnerIdAndNameAndIdNot(ownerId, name, technologyId)) {
      throw new DuplicatePortfolioNameException("Technology", name);
    }

    technology.setName(name);
    technology.setCategory(portfolioValidator.normalizeOptionalText(request.category()));
    return portfolioMapper.toTechnologyResponse(technology);
  }

  @Override
  public void delete(UUID ownerId, UUID technologyId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(technologyId, "technologyId must not be null");
    Technology technology = requireOwnedTechnology(ownerId, technologyId);
    technologyRepository.delete(technology);
  }

  @Override
  @Transactional(readOnly = true)
  public TechnologyResponse get(UUID ownerId, UUID technologyId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(technologyId, "technologyId must not be null");
    return portfolioMapper.toTechnologyResponse(requireOwnedTechnology(ownerId, technologyId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<TechnologyResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return technologyRepository.findByOwnerIdOrderByNameAsc(ownerId).stream()
        .map(portfolioMapper::toTechnologyResponse)
        .toList();
  }

  private Technology requireOwnedTechnology(UUID ownerId, UUID technologyId) {
    return technologyRepository
        .findByIdAndOwnerId(technologyId, ownerId)
        .orElseThrow(() -> new TechnologyNotFoundException(technologyId));
  }
}
