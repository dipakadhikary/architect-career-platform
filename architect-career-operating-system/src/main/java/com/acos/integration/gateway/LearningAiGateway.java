package com.acos.integration.gateway;

import com.acos.integration.capability.LearningAiCapability;
import com.acos.integration.client.LearningAiClient;
import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;
import com.acos.integration.support.AiPlatformInvoker;
import com.acos.integration.support.AiPlatformMockResponses;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Learning AI gateway. Routes to Feign when enabled, otherwise returns mocked successful responses.
 */
@Service
public class LearningAiGateway implements LearningAiCapability {

  private static final String FEATURE = "learning";

  private final ObjectProvider<LearningAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client (absent when AI Platform is disabled)
   * @param invoker AI Platform invoker
   */
  public LearningAiGateway(ObjectProvider<LearningAiClient> client, AiPlatformInvoker invoker) {
    this.client = client;
    this.invoker = invoker;
  }

  /**
   * Generates a quiz.
   *
   * @param request quiz request
   * @return quiz response
   */
  @Override
  public QuizResponse generateQuiz(QuizRequest request) {
    return invoker.execute(
        FEATURE,
        "generate-quiz",
        "/api/v1/ai/learning/quiz/generate",
        () -> requireClient().generateQuiz(request),
        () -> AiPlatformMockResponses.generateQuiz(request));
  }

  /**
   * Recommends the next learning topic.
   *
   * @param request recommendation request
   * @return recommendation response
   */
  @Override
  public RecommendNextTopicResponse recommendNextTopic(RecommendNextTopicRequest request) {
    return invoker.execute(
        FEATURE,
        "recommend-next-topic",
        "/api/v1/ai/learning/topics/recommend-next",
        () -> requireClient().recommendNextTopic(request),
        () -> AiPlatformMockResponses.recommendNextTopic(request));
  }

  /**
   * Evaluates learning progress.
   *
   * @param request evaluation request
   * @return evaluation response
   */
  @Override
  public EvaluateProgressResponse evaluateProgress(EvaluateProgressRequest request) {
    return invoker.execute(
        FEATURE,
        "evaluate-progress",
        "/api/v1/ai/learning/progress/evaluate",
        () -> requireClient().evaluateProgress(request),
        () -> AiPlatformMockResponses.evaluateProgress(request));
  }

  private LearningAiClient requireClient() {
    LearningAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new IllegalStateException("LearningAiClient is not available");
    }
    return resolved;
  }
}
