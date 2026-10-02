package com.acos.integration.capability;

import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;

/** Learning AI capability port for the Business Platform. */
public interface LearningAiCapability {

  /**
   * Generates a quiz.
   *
   * @param request quiz request
   * @return quiz response
   */
  QuizResponse generateQuiz(QuizRequest request);

  /**
   * Recommends the next learning topic.
   *
   * @param request recommendation request
   * @return recommendation response
   */
  RecommendNextTopicResponse recommendNextTopic(RecommendNextTopicRequest request);

  /**
   * Evaluates learning progress.
   *
   * @param request evaluation request
   * @return evaluation response
   */
  EvaluateProgressResponse evaluateProgress(EvaluateProgressRequest request);
}
