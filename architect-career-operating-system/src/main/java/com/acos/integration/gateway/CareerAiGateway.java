package com.acos.integration.gateway;

import com.acos.integration.capability.CareerAiCapability;
import com.acos.integration.client.CareerAiClient;
import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.support.AiPlatformInvoker;
import com.acos.integration.support.AiPlatformMockResponses;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Career AI gateway. Routes to Feign when enabled, otherwise returns mocked successful responses.
 */
@Service
public class CareerAiGateway implements CareerAiCapability {

  private static final String FEATURE = "career";

  private final ObjectProvider<CareerAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client (absent when AI Platform is disabled)
   * @param invoker AI Platform invoker
   */
  public CareerAiGateway(ObjectProvider<CareerAiClient> client, AiPlatformInvoker invoker) {
    this.client = client;
    this.invoker = invoker;
  }

  /**
   * Generates a resume.
   *
   * @param request resume request
   * @return resume response
   */
  @Override
  public ResumeResponse generateResume(ResumeRequest request) {
    return invoker.execute(
        FEATURE,
        "generate-resume",
        "/api/v1/ai/career/resume/generate",
        () -> requireClient().generateResume(request),
        () -> AiPlatformMockResponses.generateResume(request));
  }

  /**
   * Analyzes an interview.
   *
   * @param request analysis request
   * @return analysis response
   */
  @Override
  public InterviewAnalysisResponse analyzeInterview(InterviewAnalysisRequest request) {
    return invoker.execute(
        FEATURE,
        "analyze-interview",
        "/api/v1/ai/career/interview/analyze",
        () -> requireClient().analyzeInterview(request),
        () -> AiPlatformMockResponses.analyzeInterview(request));
  }

  /**
   * Generates a cover letter.
   *
   * @param request cover letter request
   * @return cover letter response
   */
  @Override
  public CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
    return invoker.execute(
        FEATURE,
        "generate-cover-letter",
        "/api/v1/ai/career/cover-letter/generate",
        () -> requireClient().generateCoverLetter(request),
        () -> AiPlatformMockResponses.generateCoverLetter(request));
  }

  private CareerAiClient requireClient() {
    CareerAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new IllegalStateException("CareerAiClient is not available");
    }
    return resolved;
  }
}
