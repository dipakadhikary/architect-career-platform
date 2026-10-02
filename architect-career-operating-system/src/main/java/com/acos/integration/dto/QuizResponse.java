package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 * Response containing a generated quiz.
 *
 * @param quizId generated quiz identifier
 * @param topic quiz topic
 * @param questions generated questions
 */
@Schema(name = "QuizResponse", description = "Generated quiz payload")
public record QuizResponse(
    @Schema(description = "Generated quiz identifier") UUID quizId,
    @Schema(description = "Quiz topic") String topic,
    @Schema(description = "Generated questions") List<QuizQuestion> questions) {

  /**
   * A single quiz question.
   *
   * @param prompt question prompt
   * @param choices optional multiple-choice options
   * @param correctAnswer expected answer
   * @param explanation optional explanation
   */
  @Schema(name = "QuizQuestion", description = "A single quiz question")
  public record QuizQuestion(
      @Schema(description = "Question prompt") String prompt,
      @Schema(description = "Optional multiple-choice options") List<String> choices,
      @Schema(description = "Expected answer") String correctAnswer,
      @Schema(description = "Optional explanation") String explanation) {}
}
