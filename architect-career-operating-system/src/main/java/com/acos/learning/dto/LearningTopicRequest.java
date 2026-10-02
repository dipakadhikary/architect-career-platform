package com.acos.learning.dto;

import com.acos.learning.entity.TopicStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload used to create or update a learning topic.
 *
 * @param title topic title
 * @param description optional description
 * @param status optional status; defaults to {@code NOT_STARTED} on create
 * @param sortOrder optional sort order; when null on create, appended at the end
 */
@Schema(
    name = "LearningTopicRequest",
    description = "Payload used to create or update a learning topic")
public record LearningTopicRequest(
    @Schema(
            description = "Topic title",
            example = "CAP Theorem",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 200, message = "title must not exceed 200 characters") String title,
    @Schema(
            description = "Optional description",
            example = "Trade-offs between consistency, availability, and partition tolerance",
            maxLength = 2000,
            nullable = true)
        @Size(max = 2000, message = "description must not exceed 2000 characters") String description,
    @Schema(
            description = "Optional topic status; defaults to NOT_STARTED on create",
            example = "NOT_STARTED",
            nullable = true)
        TopicStatus status,
    @Schema(
            description = "Optional sort order; omitted on create appends at the end",
            example = "0",
            nullable = true)
        Integer sortOrder) {}
