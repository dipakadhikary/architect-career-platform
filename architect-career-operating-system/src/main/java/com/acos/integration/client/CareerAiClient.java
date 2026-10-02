package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Career capability contract with the AI Platform. */
@FeignClient(
    name = "career-ai",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface CareerAiClient {

  /**
   * Generates a resume tailored to a target role.
   *
   * @param request resume generation request
   * @return generated resume
   */
  @PostMapping(
      value = "/api/v1/ai/career/resume/generate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResumeResponse generateResume(@RequestBody ResumeRequest request);

  /**
   * Analyzes an interview transcript.
   *
   * @param request interview analysis request
   * @return analysis result
   */
  @PostMapping(
      value = "/api/v1/ai/career/interview/analyze",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  InterviewAnalysisResponse analyzeInterview(@RequestBody InterviewAnalysisRequest request);

  /**
   * Generates a cover letter for a target role.
   *
   * @param request cover letter request
   * @return generated cover letter
   */
  @PostMapping(
      value = "/api/v1/ai/career/cover-letter/generate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  CoverLetterResponse generateCoverLetter(@RequestBody CoverLetterRequest request);
}
