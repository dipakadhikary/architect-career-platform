package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * One AI-generated question. Difficulty is draft metadata, not a published taxonomy change.
 *
 * @param question question text
 * @param answer answer text
 * @param difficulty BEGINNER, INTERMEDIATE, or ADVANCED
 * @param explanation optional explanation
 */
@Schema(name = "AuthoringQuestionResponse", description = "Structured question draft")
public record AuthoringQuestionResponse(
    String question, String answer, String difficulty, String explanation) {}
