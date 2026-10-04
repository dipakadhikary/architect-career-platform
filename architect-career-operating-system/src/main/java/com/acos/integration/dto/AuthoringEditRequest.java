package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Edited draft text. Saving this text to ACOS is a separate knowledge update.
 *
 * @param content replacement markdown
 */
@Schema(name = "AuthoringEditRequest", description = "Edit an AI draft")
public record AuthoringEditRequest(@NotBlank String content) {}
