package com.acos.tutorial.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "TutorialQuestionResponse")
public record TutorialQuestionResponse(
    UUID id, UUID topicId, String question, String answer, int sortOrder, Instant updatedAt) {}
