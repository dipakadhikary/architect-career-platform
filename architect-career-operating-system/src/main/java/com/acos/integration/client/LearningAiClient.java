package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.EvaluateProgressRequest;
import com.acos.integration.dto.EvaluateProgressResponse;
import com.acos.integration.dto.QuizRequest;
import com.acos.integration.dto.QuizResponse;
import com.acos.integration.dto.RecommendNextTopicRequest;
import com.acos.integration.dto.RecommendNextTopicResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Learning capability contract with the AI Platform. */
@FeignClient(
    name = "learning-ai",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface LearningAiClient {

  /**
   * Generates a quiz for a learning topic.
   *
   * @param request quiz generation request
   * @return generated quiz
   */
  @PostMapping(
      value = "/api/v1/ai/learning/quiz/generate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  QuizResponse generateQuiz(@RequestBody QuizRequest request);

  /**
   * Recommends the next learning topic.
   *
   * @param request recommendation request
   * @return next topic recommendation
   */
  @PostMapping(
      value = "/api/v1/ai/learning/topics/recommend-next",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  RecommendNextTopicResponse recommendNextTopic(@RequestBody RecommendNextTopicRequest request);

  /**
   * Evaluates learning progress.
   *
   * @param request progress evaluation request
   * @return progress evaluation result
   */
  @PostMapping(
      value = "/api/v1/ai/learning/progress/evaluate",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  EvaluateProgressResponse evaluateProgress(@RequestBody EvaluateProgressRequest request);
}
