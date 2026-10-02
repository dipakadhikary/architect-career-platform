package com.acos.career.ai;

import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.facade.CareerAiFacade;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Career AI extension points. AI is never invoked automatically by career workflows; callers opt in
 * through these service methods.
 *
 * <p>Depends only on {@link CareerAiFacade}; never on OpenFeign clients.
 */
@Service
public class CareerAiService {

  private static final Logger LOG = LoggerFactory.getLogger(CareerAiService.class);

  private final CareerAiFacade careerAiFacade;

  /**
   * Creates the career AI service.
   *
   * @param careerAiFacade career AI facade
   */
  public CareerAiService(CareerAiFacade careerAiFacade) {
    this.careerAiFacade = Objects.requireNonNull(careerAiFacade, "careerAiFacade must not be null");
  }

  /**
   * Generates a resume via the AI facade.
   *
   * @param request resume request
   * @return resume response ({@code AI service unavailable} fallback when AI is down)
   */
  public ResumeResponse generateResume(ResumeRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return careerAiFacade.generateResume(request);
  }

  /**
   * Analyzes an interview via the AI facade.
   *
   * @param request analysis request
   * @return analysis response ({@code AI service unavailable} fallback when AI is down)
   */
  public InterviewAnalysisResponse analyzeInterview(InterviewAnalysisRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return careerAiFacade.analyzeInterview(request);
  }

  /**
   * Generates a cover letter via the AI facade.
   *
   * @param request cover letter request
   * @return cover letter response ({@code AI service unavailable} fallback when AI is down)
   */
  public CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return careerAiFacade.generateCoverLetter(request);
  }

  /**
   * Extension point for future career recommendations. Reserved until the AI Platform contract
   * exposes the capability.
   *
   * @param userId owning user id
   * @param targetRole optional target role
   * @return empty until the AI Platform capability is available
   */
  public Optional<CareerRecommendationResult> recommendCareer(UUID userId, String targetRole) {
    Objects.requireNonNull(userId, "userId must not be null");
    if (LOG.isDebugEnabled()) {
      LOG.debug(
          "Career recommendation extension point reserved userId={} targetRole={}",
          userId,
          targetRole);
    }
    return Optional.empty();
  }

  /**
   * Placeholder result for the future career recommendation capability.
   *
   * @param summary recommendation summary
   */
  public record CareerRecommendationResult(String summary) {}
}
