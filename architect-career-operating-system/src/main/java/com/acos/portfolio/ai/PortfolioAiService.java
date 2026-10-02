package com.acos.portfolio.ai;

import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;
import com.acos.integration.facade.PortfolioAiFacade;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Portfolio AI extension points. AI is never invoked automatically by portfolio workflows; callers
 * opt in through these service methods.
 *
 * <p>Depends only on {@link PortfolioAiFacade}; never on OpenFeign clients.
 */
@Service
public class PortfolioAiService {

  private static final Logger LOG = LoggerFactory.getLogger(PortfolioAiService.class);

  private final PortfolioAiFacade portfolioAiFacade;

  /**
   * Creates the portfolio AI service.
   *
   * @param portfolioAiFacade portfolio AI facade
   */
  public PortfolioAiService(PortfolioAiFacade portfolioAiFacade) {
    this.portfolioAiFacade =
        Objects.requireNonNull(portfolioAiFacade, "portfolioAiFacade must not be null");
  }

  /**
   * Reviews a portfolio via the AI facade.
   *
   * @param request review request
   * @return review response (empty analysis fallback when AI is unavailable)
   */
  public PortfolioReviewResponse reviewPortfolio(PortfolioReviewRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return portfolioAiFacade.reviewPortfolio(request);
  }

  /**
   * Analyzes skill gaps via the AI facade.
   *
   * @param request skill-gap request
   * @return skill-gap response (empty analysis fallback when AI is unavailable)
   */
  public SkillGapResponse analyzeSkillGap(SkillGapRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return portfolioAiFacade.analyzeSkillGap(request);
  }

  /**
   * Extension point for future project summary generation. Reserved until the AI Platform contract
   * exposes the capability.
   *
   * @param userId owning user id
   * @param projectId project id
   * @return empty until the AI Platform capability is available
   */
  public Optional<ProjectSummaryResult> generateProjectSummary(UUID userId, UUID projectId) {
    Objects.requireNonNull(userId, "userId must not be null");
    Objects.requireNonNull(projectId, "projectId must not be null");
    if (LOG.isDebugEnabled()) {
      LOG.debug(
          "Project summary extension point reserved userId={} projectId={}", userId, projectId);
    }
    return Optional.empty();
  }

  /**
   * Placeholder result for the future project summary capability.
   *
   * @param summary generated summary
   */
  public record ProjectSummaryResult(String summary) {}
}
