package com.acos.integration.gateway;

import com.acos.integration.capability.PortfolioAiCapability;
import com.acos.integration.client.PortfolioAiClient;
import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;
import com.acos.integration.support.AiPlatformInvoker;
import com.acos.integration.support.AiPlatformMockResponses;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Portfolio AI gateway. Routes to Feign when enabled, otherwise returns mocked successful
 * responses.
 */
@Service
public class PortfolioAiGateway implements PortfolioAiCapability {

  private static final String FEATURE = "portfolio";

  private final ObjectProvider<PortfolioAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client (absent when AI Platform is disabled)
   * @param invoker AI Platform invoker
   */
  public PortfolioAiGateway(ObjectProvider<PortfolioAiClient> client, AiPlatformInvoker invoker) {
    this.client = client;
    this.invoker = invoker;
  }

  /**
   * Reviews a portfolio.
   *
   * @param request review request
   * @return review response
   */
  @Override
  public PortfolioReviewResponse reviewPortfolio(PortfolioReviewRequest request) {
    return invoker.execute(
        FEATURE,
        "review-portfolio",
        "/api/v1/ai/portfolio/review",
        () -> requireClient().reviewPortfolio(request),
        () -> AiPlatformMockResponses.reviewPortfolio(request));
  }

  /**
   * Analyzes skill gaps.
   *
   * @param request skill-gap request
   * @return skill-gap response
   */
  @Override
  public SkillGapResponse analyzeSkillGap(SkillGapRequest request) {
    return invoker.execute(
        FEATURE,
        "analyze-skill-gap",
        "/api/v1/ai/portfolio/skill-gap/analyze",
        () -> requireClient().analyzeSkillGap(request),
        () -> AiPlatformMockResponses.analyzeSkillGap(request));
  }

  private PortfolioAiClient requireClient() {
    PortfolioAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new IllegalStateException("PortfolioAiClient is not available");
    }
    return resolved;
  }
}
