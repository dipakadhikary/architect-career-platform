package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Optional new instructions for a new proposal. The previous proposal is kept.
 *
 * @param instructions replacement instructions, or null to reuse the previous ones
 */
@Schema(name = "AuthoringRegenerateRequest", description = "Regenerate an AI draft")
public record AuthoringRegenerateRequest(String instructions) {}
