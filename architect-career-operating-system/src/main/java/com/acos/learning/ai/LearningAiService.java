package com.acos.learning.ai;

import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;
import com.acos.integration.facade.LearningAiFacade;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Learning AI extension points. AI is never invoked automatically; callers opt in through these
 * service methods.
 *
 * <p>Depends only on {@link LearningAiFacade}; never on OpenFeign clients.
 */
@Service
public class LearningAiService {

  private final LearningAiFacade learningAiFacade;

  /**
   * Creates the learning AI service.
   *
   * @param learningAiFacade learning AI facade
   */
  public LearningAiService(LearningAiFacade learningAiFacade) {
    this.learningAiFacade =
        Objects.requireNonNull(learningAiFacade, "learningAiFacade must not be null");
  }

  /**
   * Generates a quiz via the AI facade.
   *
   * @param request quiz request
   * @return quiz response (empty fallback when AI is unavailable)
   */
  public QuizResponse generateQuiz(QuizRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return learningAiFacade.generateQuiz(request);
  }

  /**
   * Recommends the next topic via the AI facade.
   *
   * @param request recommendation request
   * @return recommendation (empty fallback when AI is unavailable)
   */
  public RecommendNextTopicResponse recommendNextTopic(RecommendNextTopicRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return learningAiFacade.recommendNextTopic(request);
  }

  /**
   * Evaluates learning progress via the AI facade.
   *
   * @param request evaluation request
   * @return evaluation (empty fallback when AI is unavailable)
   */
  public EvaluateProgressResponse evaluateProgress(EvaluateProgressRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return learningAiFacade.evaluateProgress(request);
  }
}
