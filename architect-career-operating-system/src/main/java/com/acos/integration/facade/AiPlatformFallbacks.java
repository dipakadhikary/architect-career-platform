package com.acos.integration.facade;

import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.dto.SkillGapResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Graceful fallback payloads used when AI is disabled or unavailable so business flows never fail.
 */
public final class AiPlatformFallbacks {

  static final String UNAVAILABLE_MESSAGE = "AI service unavailable";

  private AiPlatformFallbacks() {}

  /**
   * Successful indexing acknowledgement when AI indexing is skipped or unavailable.
   *
   * @param request original request
   * @return acknowledgement response
   */
  public static KnowledgeIndexResponse indexAcknowledged(KnowledgeIndexRequest request) {
    return new KnowledgeIndexResponse(
        UUID.randomUUID(), request.noteId(), "ACKNOWLEDGED", Instant.now());
  }

  /**
   * Empty knowledge search result.
   *
   * @return empty search response
   */
  public static KnowledgeSearchResponse emptySearch() {
    return new KnowledgeSearchResponse(List.of());
  }

  /**
   * Empty knowledge summary.
   *
   * @param request original request
   * @return empty summary response
   */
  public static KnowledgeSummarizeResponse emptySummary(KnowledgeSummarizeRequest request) {
    return new KnowledgeSummarizeResponse(request.noteId(), "", List.of());
  }

  /**
   * Empty quiz payload.
   *
   * @param request original request
   * @return empty quiz response
   */
  public static QuizResponse emptyQuiz(QuizRequest request) {
    return new QuizResponse(null, request.topic() == null ? "" : request.topic(), List.of());
  }

  /**
   * Empty next-topic recommendation.
   *
   * @return empty recommendation response
   */
  public static RecommendNextTopicResponse emptyRecommendation() {
    return new RecommendNextTopicResponse("", "", List.of());
  }

  /**
   * Empty progress evaluation.
   *
   * @return empty evaluation response
   */
  public static EvaluateProgressResponse emptyProgress() {
    return new EvaluateProgressResponse(0.0d, "", List.of(), List.of());
  }

  /**
   * Unavailable resume payload.
   *
   * @param request original request
   * @return unavailable resume response
   */
  public static ResumeResponse unavailableResume(ResumeRequest request) {
    return new ResumeResponse(UNAVAILABLE_MESSAGE, "plain");
  }

  /**
   * Unavailable interview analysis payload.
   *
   * @param request original request
   * @return unavailable analysis response
   */
  public static InterviewAnalysisResponse unavailableInterviewAnalysis(
      InterviewAnalysisRequest request) {
    return new InterviewAnalysisResponse(UNAVAILABLE_MESSAGE, List.of(), List.of(), null);
  }

  /**
   * Unavailable cover letter payload.
   *
   * @param request original request
   * @return unavailable cover letter response
   */
  public static CoverLetterResponse unavailableCoverLetter(CoverLetterRequest request) {
    return new CoverLetterResponse(UNAVAILABLE_MESSAGE, "plain");
  }

  /**
   * Empty portfolio review.
   *
   * @return empty review response
   */
  public static PortfolioReviewResponse emptyPortfolioReview() {
    return new PortfolioReviewResponse("", List.of(), List.of(), null);
  }

  /**
   * Empty skill-gap analysis.
   *
   * @return empty skill-gap response
   */
  public static SkillGapResponse emptySkillGap() {
    return new SkillGapResponse("", List.of(), List.of());
  }
}
