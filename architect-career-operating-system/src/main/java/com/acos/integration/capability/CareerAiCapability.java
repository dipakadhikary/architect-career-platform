package com.acos.integration.capability;

import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;

/** Career AI capability port for the Business Platform. */
public interface CareerAiCapability {

  /**
   * Generates a resume.
   *
   * @param request resume request
   * @return resume response
   */
  ResumeResponse generateResume(ResumeRequest request);

  /**
   * Analyzes an interview.
   *
   * @param request analysis request
   * @return analysis response
   */
  InterviewAnalysisResponse analyzeInterview(InterviewAnalysisRequest request);

  /**
   * Generates a cover letter.
   *
   * @param request cover letter request
   * @return cover letter response
   */
  CoverLetterResponse generateCoverLetter(CoverLetterRequest request);
}
