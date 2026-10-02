package com.acos.integration.support;

import com.acos.integration.dto.CoverLetterRequest;
import com.acos.integration.dto.CoverLetterResponse;
import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.InterviewAnalysisRequest;
import com.acos.integration.dto.InterviewAnalysisResponse;
import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.dto.KnowledgeSummarizeRequest;
import com.acos.integration.dto.KnowledgeSummarizeResponse;
import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.dto.SkillGapRequest;
import com.acos.integration.dto.SkillGapResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Mocked successful AI Platform responses used when {@code ai.platform.enabled=false}. */
public final class AiPlatformMockResponses {

  private AiPlatformMockResponses() {}

  /**
   * Builds a mocked knowledge index response.
   *
   * @param request original request
   * @return mocked response
   */
  public static KnowledgeIndexResponse indexKnowledge(KnowledgeIndexRequest request) {
    return new KnowledgeIndexResponse(
        UUID.randomUUID(), request.noteId(), "INDEXED", Instant.now());
  }

  /**
   * Builds a mocked knowledge search response.
   *
   * @param request original request
   * @return mocked response
   */
  public static KnowledgeSearchResponse searchKnowledge(KnowledgeSearchRequest request) {
    return new KnowledgeSearchResponse(
        List.of(
            new KnowledgeSearchResponse.KnowledgeSearchHit(
                UUID.randomUUID(),
                "Mocked search hit",
                "Mocked snippet for query: " + request.query(),
                1.0d)));
  }

  /**
   * Builds a mocked knowledge summarize response.
   *
   * @param request original request
   * @return mocked response
   */
  public static KnowledgeSummarizeResponse summarizeKnowledge(KnowledgeSummarizeRequest request) {
    return new KnowledgeSummarizeResponse(
        request.noteId(), "Mocked summary for knowledge content.", List.of("Mocked key point"));
  }

  /**
   * Builds a mocked quiz response.
   *
   * @param request original request
   * @return mocked response
   */
  public static QuizResponse generateQuiz(QuizRequest request) {
    return new QuizResponse(
        UUID.randomUUID(),
        request.topic(),
        List.of(
            new QuizResponse.QuizQuestion(
                "Mocked question for " + request.topic() + "?",
                List.of("A", "B", "C", "D"),
                "A",
                "Mocked explanation")));
  }

  /**
   * Builds a mocked next-topic recommendation.
   *
   * @param request original request
   * @return mocked response
   */
  public static RecommendNextTopicResponse recommendNextTopic(RecommendNextTopicRequest request) {
    return new RecommendNextTopicResponse(
        "Mocked next topic",
        "Mocked recommendation while AI Platform is disabled.",
        List.of("Follow-up topic"));
  }

  /**
   * Builds a mocked progress evaluation.
   *
   * @param request original request
   * @return mocked response
   */
  public static EvaluateProgressResponse evaluateProgress(EvaluateProgressRequest request) {
    return new EvaluateProgressResponse(
        50.0d,
        "Mocked progress evaluation.",
        List.of("Consistent practice"),
        List.of("Deepen system design"));
  }

  /**
   * Builds a mocked resume response.
   *
   * @param request original request
   * @return mocked response
   */
  public static ResumeResponse generateResume(ResumeRequest request) {
    return new ResumeResponse(
        "# Mocked Resume\n\nTarget role: " + request.targetRole(), "markdown");
  }

  /**
   * Builds a mocked interview analysis response.
   *
   * @param request original request
   * @return mocked response
   */
  public static InterviewAnalysisResponse analyzeInterview(InterviewAnalysisRequest request) {
    return new InterviewAnalysisResponse(
        "Mocked interview analysis.",
        List.of("Clear communication"),
        List.of("Add quantified impact"),
        75.0d);
  }

  /**
   * Builds a mocked cover letter response.
   *
   * @param request original request
   * @return mocked response
   */
  public static CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
    return new CoverLetterResponse(
        "Mocked cover letter for " + request.targetRole() + ".", "markdown");
  }

  /**
   * Builds a mocked portfolio review response.
   *
   * @param request original request
   * @return mocked response
   */
  public static PortfolioReviewResponse reviewPortfolio(PortfolioReviewRequest request) {
    return new PortfolioReviewResponse(
        "Mocked portfolio review.",
        List.of("Strong project narrative"),
        List.of("Add measurable outcomes"),
        80.0d);
  }

  /**
   * Builds a mocked skill-gap response.
   *
   * @param request original request
   * @return mocked response
   */
  public static SkillGapResponse analyzeSkillGap(SkillGapRequest request) {
    return new SkillGapResponse(
        "Mocked skill-gap analysis for " + request.targetRole() + ".",
        List.of("Distributed systems leadership"),
        List.of("Complete a large-scale design case study"));
  }
}
