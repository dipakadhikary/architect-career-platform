package com.acos.integration.facade;

import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;
import com.acos.integration.gateway.LearningAiGateway;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Learning AI facade between business services and the OpenFeign learning client. Returns empty
 * recommendations/results when AI is disabled or unavailable.
 */
@Service
public class LearningAiFacade {

  private final LearningAiGateway learningAiGateway;
  private final AiFacadeSupport aiFacadeSupport;

  /**
   * Creates the facade.
   *
   * @param learningAiGateway learning gateway
   * @param aiFacadeSupport shared facade support
   */
  public LearningAiFacade(LearningAiGateway learningAiGateway, AiFacadeSupport aiFacadeSupport) {
    this.learningAiGateway =
        Objects.requireNonNull(learningAiGateway, "learningAiGateway must not be null");
    this.aiFacadeSupport =
        Objects.requireNonNull(aiFacadeSupport, "aiFacadeSupport must not be null");
  }

  /**
   * Generates a quiz with an empty fallback.
   *
   * @param request quiz request
   * @return quiz response
   */
  public QuizResponse generateQuiz(QuizRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");
    Objects.requireNonNull(request.topic(), "topic must not be null");
    if (request.topic().isBlank()) {
      throw new IllegalArgumentException("topic must not be blank");
    }

    return aiFacadeSupport.execute(
        "learning",
        "generate-quiz",
        () -> learningAiGateway.generateQuiz(request),
        () -> AiPlatformFallbacks.emptyQuiz(request),
        null);
  }

  /**
   * Recommends the next topic with an empty recommendation fallback.
   *
   * @param request recommendation request
   * @return recommendation response
   */
  public RecommendNextTopicResponse recommendNextTopic(RecommendNextTopicRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");

    return aiFacadeSupport.execute(
        "learning",
        "recommend-next-topic",
        () -> learningAiGateway.recommendNextTopic(request),
        AiPlatformFallbacks::emptyRecommendation,
        null);
  }

  /**
   * Evaluates learning progress with an empty evaluation fallback.
   *
   * @param request evaluation request
   * @return evaluation response
   */
  public EvaluateProgressResponse evaluateProgress(EvaluateProgressRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    Objects.requireNonNull(request.userId(), "userId must not be null");

    return aiFacadeSupport.execute(
        "learning",
        "evaluate-progress",
        () -> learningAiGateway.evaluateProgress(request),
        AiPlatformFallbacks::emptyProgress,
        null);
  }
}
