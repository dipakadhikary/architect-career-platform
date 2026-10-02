package com.acos.learning.dto;

import com.acos.learning.entity.TopicStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Payload used to update a learning topic status.
 *
 * @param status new topic status
 */
@Schema(name = "TopicStatusUpdateRequest", description = "Payload used to update topic status")
public record TopicStatusUpdateRequest(
    @Schema(
            description = "New topic status",
            example = "COMPLETED",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "status must not be null") TopicStatus status) {}
