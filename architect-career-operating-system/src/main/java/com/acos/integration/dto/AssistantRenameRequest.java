package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rename payload. Ownership is checked from the authenticated caller.
 *
 * @param title replacement title
 */
@Schema(name = "AssistantRenameRequest", description = "Rename an AI conversation")
public record AssistantRenameRequest(
    @Schema(description = "Replacement title", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "title must not be blank") @Size(max = 120, message = "title is too long") String title) {}
