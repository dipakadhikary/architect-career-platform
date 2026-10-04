package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Conversation owned by the authenticated caller.
 *
 * @param id conversation id
 * @param title display title
 * @param createdAt creation time
 * @param updatedAt last update time
 */
@Schema(name = "AssistantConversationResponse", description = "An ACOS AI conversation")
public record AssistantConversationResponse(
    String id, String title, String createdAt, String updatedAt) {}
