package com.acos.integration.facade;

import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.gateway.CareerAiGateway;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Career AI facade between business services and the OpenFeign career client. Returns an "AI
 * service unavailable" payload when AI is disabled or unavailable.
 */
@Service
public class CareerAiFacade {

  private final CareerAiGateway careerAiGateway;
  private final AiFacadeSupport aiFacadeSupport;

  /**
   * Creates the facade.
   *
   * @param careerAiGateway career gateway
   * @param aiFacadeSupport shared facade support
   */
  public CareerAiFacade(CareerAiGateway careerAiGateway, AiFacadeSupport aiFacadeSupport) {
    this.careerAiGateway =
        Objects.requireNonNull(careerAiGateway, "careerAiGateway must not be null");
    this.aiFacadeSupport =
        Objects.requireNonNull(aiFacadeSupport, "aiFacadeSupport must not be null");
  }

  /**
   * Generates a resume with an unavailable fallback message.
   *
   * @param request resume request
   * @return resume response
   */
  public ResumeResponse generateResume(ResumeRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.targetRole(), "targetRole must not be null");
    if (request.targetRole().isBlank()) {
      throw new IllegalArgumentException("targetRole must not be blank");
    }

    return aiFacadeSupport.execute(
        "career",
        "generate-resume",
        () -> careerAiGateway.generateResume(request),
        () -> AiPlatformFallbacks.unavailableResume(request),
        null);
  }

  /**
   * Analyzes an interview with an unavailable fallback message.
   *
   * @param request analysis request
   * @return analysis response
   */
  public InterviewAnalysisResponse analyzeInterview(InterviewAnalysisRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.transcript(), "transcript must not be null");

    return aiFacadeSupport.execute(
        "career",
        "analyze-interview",
        () -> careerAiGateway.analyzeInterview(request),
        () -> AiPlatformFallbacks.unavailableInterviewAnalysis(request),
        null);
  }

  /**
   * Generates a cover letter with an unavailable fallback message.
   *
   * @param request cover letter request
   * @return cover letter response
   */
  public CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.targetRole(), "targetRole must not be null");
    if (request.targetRole().isBlank()) {
      throw new IllegalArgumentException("targetRole must not be blank");
    }

    return aiFacadeSupport.execute(
        "career",
        "generate-cover-letter",
        () -> careerAiGateway.generateCoverLetter(request),
        () -> AiPlatformFallbacks.unavailableCoverLetter(request),
        null);
  }
}
