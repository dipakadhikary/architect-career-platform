package com.acos.integration.capability;

import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;

/** Portfolio AI capability port for the Business Platform. */
public interface PortfolioAiCapability {

  /**
   * Reviews a portfolio.
   *
   * @param request review request
   * @return review response
   */
  PortfolioReviewResponse reviewPortfolio(PortfolioReviewRequest request);

  /**
   * Analyzes skill gaps.
   *
   * @param request skill-gap request
   * @return skill-gap response
   */
  SkillGapResponse analyzeSkillGap(SkillGapRequest request);
}
