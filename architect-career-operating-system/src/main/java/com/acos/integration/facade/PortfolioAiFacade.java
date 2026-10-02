package com.acos.integration.facade;

import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;
import com.acos.integration.gateway.PortfolioAiGateway;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Portfolio AI facade between business services and the OpenFeign portfolio client. Returns empty
 * analysis payloads when AI is disabled or unavailable.
 */
@Service
public class PortfolioAiFacade {

  private final PortfolioAiGateway portfolioAiGateway;
  private final AiFacadeSupport aiFacadeSupport;

  /**
   * Creates the facade.
   *
   * @param portfolioAiGateway portfolio gateway
   * @param aiFacadeSupport shared facade support
   */
  public PortfolioAiFacade(PortfolioAiGateway portfolioAiGateway, AiFacadeSupport aiFacadeSupport) {
    this.portfolioAiGateway =
        Objects.requireNonNull(portfolioAiGateway, "portfolioAiGateway must not be null");
    this.aiFacadeSupport =
        Objects.requireNonNull(aiFacadeSupport, "aiFacadeSupport must not be null");
  }

  /**
   * Reviews a portfolio with an empty analysis fallback.
   *
   * @param request review request
   * @return review response
   */
  public PortfolioReviewResponse reviewPortfolio(PortfolioReviewRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");

    return aiFacadeSupport.execute(
        "portfolio",
        "review-portfolio",
        () -> portfolioAiGateway.reviewPortfolio(request),
        AiPlatformFallbacks::emptyPortfolioReview,
        null);
  }

  /**
   * Analyzes skill gaps with an empty analysis fallback.
   *
   * @param request skill-gap request
   * @return skill-gap response
   */
  public SkillGapResponse analyzeSkillGap(SkillGapRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.targetRole(), "targetRole must not be null");
    if (request.targetRole().isBlank()) {
      throw new IllegalArgumentException("targetRole must not be blank");
    }

    return aiFacadeSupport.execute(
        "portfolio",
        "analyze-skill-gap",
        () -> portfolioAiGateway.analyzeSkillGap(request),
        AiPlatformFallbacks::emptySkillGap,
        null);
  }
}
