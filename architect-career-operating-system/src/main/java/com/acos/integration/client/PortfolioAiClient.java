package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Portfolio capability contract with the AI Platform. */
@FeignClient(
    name = "portfolio-ai",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface PortfolioAiClient {

  /**
   * Reviews a portfolio for strengths and gaps.
   *
   * @param request portfolio review request
   * @return review result
   */
  @PostMapping(
      value = "/api/v1/ai/portfolio/review",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  PortfolioReviewResponse reviewPortfolio(@RequestBody PortfolioReviewRequest request);

  /**
   * Analyzes skill gaps relative to a target role.
   *
   * @param request skill-gap analysis request
   * @return skill-gap analysis result
   */
  @PostMapping(
      value = "/api/v1/ai/portfolio/skill-gap/analyze",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  SkillGapResponse analyzeSkillGap(@RequestBody SkillGapRequest request);
}
