package com.acos.portfolio.service;

import com.acos.portfolio.dto.SkillRequest;
import com.acos.portfolio.dto.SkillResponse;
import com.acos.portfolio.entity.Skill;
import com.acos.portfolio.exception.DuplicatePortfolioNameException;
import com.acos.portfolio.exception.SkillNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.SkillRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link SkillService} implementation. */
@Service
@Transactional
public class SkillServiceImpl implements SkillService {

  private final SkillRepository skillRepository;
  private final PortfolioMapper portfolioMapper;
  private final PortfolioValidator portfolioValidator;

  /**
   * Creates the skill service.
   *
   * @param skillRepository skill repository
   * @param portfolioMapper mapper
   * @param portfolioValidator validator
   */
  public SkillServiceImpl(
      SkillRepository skillRepository,
      PortfolioMapper portfolioMapper,
      PortfolioValidator portfolioValidator) {
    this.skillRepository =
        Objects.requireNonNull(skillRepository, "skillRepository must not be null");
    this.portfolioMapper =
        Objects.requireNonNull(portfolioMapper, "portfolioMapper must not be null");
    this.portfolioValidator =
        Objects.requireNonNull(portfolioValidator, "portfolioValidator must not be null");
  }

  @Override
  public SkillResponse create(UUID ownerId, SkillRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    String name = request.name().trim();
    portfolioValidator.validateYearsOfExperience(request.yearsOfExperience());
    if (skillRepository.existsByOwnerIdAndName(ownerId, name)) {
      throw new DuplicatePortfolioNameException("Skill", name);
    }

    Skill skill =
        new Skill(
            ownerId,
            name,
            request.proficiencyLevel(),
            request.yearsOfExperience(),
            portfolioValidator.normalizeOptionalText(request.description()));
    Skill saved = skillRepository.save(skill);
    return portfolioMapper.toSkillResponse(saved);
  }

  @Override
  public SkillResponse update(UUID ownerId, UUID skillId, SkillRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(skillId, "skillId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Skill skill = requireOwnedSkill(ownerId, skillId);
    String name = request.name().trim();
    portfolioValidator.validateYearsOfExperience(request.yearsOfExperience());
    if (skillRepository.existsByOwnerIdAndNameAndIdNot(ownerId, name, skillId)) {
      throw new DuplicatePortfolioNameException("Skill", name);
    }

    skill.setName(name);
    skill.setProficiencyLevel(request.proficiencyLevel());
    skill.setYearsOfExperience(request.yearsOfExperience());
    skill.setDescription(portfolioValidator.normalizeOptionalText(request.description()));
    return portfolioMapper.toSkillResponse(skill);
  }

  @Override
  public void delete(UUID ownerId, UUID skillId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(skillId, "skillId must not be null");
    Skill skill = requireOwnedSkill(ownerId, skillId);
    skillRepository.delete(skill);
  }

  @Override
  @Transactional(readOnly = true)
  public SkillResponse get(UUID ownerId, UUID skillId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(skillId, "skillId must not be null");
    return portfolioMapper.toSkillResponse(requireOwnedSkill(ownerId, skillId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<SkillResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return skillRepository.findByOwnerIdOrderByNameAsc(ownerId).stream()
        .map(portfolioMapper::toSkillResponse)
        .toList();
  }

  private Skill requireOwnedSkill(UUID ownerId, UUID skillId) {
    return skillRepository
        .findByIdAndOwnerId(skillId, ownerId)
        .orElseThrow(() -> new SkillNotFoundException(skillId));
  }
}
