package com.acos.portfolio.service;

import com.acos.portfolio.dto.AchievementRequest;
import com.acos.portfolio.dto.AchievementResponse;
import com.acos.portfolio.entity.Achievement;
import com.acos.portfolio.exception.AchievementNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.AchievementRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link AchievementService} implementation. */
@Service
@Transactional
public class AchievementServiceImpl implements AchievementService {

  private final AchievementRepository achievementRepository;
  private final PortfolioMapper portfolioMapper;
  private final PortfolioValidator portfolioValidator;

  /**
   * Creates the achievement service.
   *
   * @param achievementRepository achievement repository
   * @param portfolioMapper mapper
   * @param portfolioValidator validator
   */
  public AchievementServiceImpl(
      AchievementRepository achievementRepository,
      PortfolioMapper portfolioMapper,
      PortfolioValidator portfolioValidator) {
    this.achievementRepository =
        Objects.requireNonNull(achievementRepository, "achievementRepository must not be null");
    this.portfolioMapper =
        Objects.requireNonNull(portfolioMapper, "portfolioMapper must not be null");
    this.portfolioValidator =
        Objects.requireNonNull(portfolioValidator, "portfolioValidator must not be null");
  }

  @Override
  public AchievementResponse create(UUID ownerId, AchievementRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Achievement achievement =
        new Achievement(
            ownerId,
            request.title().trim(),
            request.description().trim(),
            request.achievedOn(),
            portfolioValidator.normalizeOptionalText(request.organization()));
    Achievement saved = achievementRepository.save(achievement);
    return portfolioMapper.toAchievementResponse(saved);
  }

  @Override
  public AchievementResponse update(UUID ownerId, UUID achievementId, AchievementRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(achievementId, "achievementId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Achievement achievement = requireOwnedAchievement(ownerId, achievementId);
    achievement.setTitle(request.title().trim());
    achievement.setDescription(request.description().trim());
    achievement.setAchievedOn(request.achievedOn());
    achievement.setOrganization(portfolioValidator.normalizeOptionalText(request.organization()));
    return portfolioMapper.toAchievementResponse(achievement);
  }

  @Override
  public void delete(UUID ownerId, UUID achievementId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(achievementId, "achievementId must not be null");
    Achievement achievement = requireOwnedAchievement(ownerId, achievementId);
    achievementRepository.delete(achievement);
  }

  @Override
  @Transactional(readOnly = true)
  public AchievementResponse get(UUID ownerId, UUID achievementId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(achievementId, "achievementId must not be null");
    return portfolioMapper.toAchievementResponse(requireOwnedAchievement(ownerId, achievementId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<AchievementResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return achievementRepository.findByOwnerIdOrderByAchievedOnDesc(ownerId).stream()
        .map(portfolioMapper::toAchievementResponse)
        .toList();
  }

  private Achievement requireOwnedAchievement(UUID ownerId, UUID achievementId) {
    return achievementRepository
        .findByIdAndOwnerId(achievementId, ownerId)
        .orElseThrow(() -> new AchievementNotFoundException(achievementId));
  }
}
