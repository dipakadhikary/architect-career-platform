package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Conversation metadata plus the latest messages.
 *
 * @param id conversation id
 * @param title display title
 * @param createdAt creation time
 * @param updatedAt last update time
 * @param messages latest messages in chronological order
 * @param truncated whether older messages were omitted
 */
@Schema(name = "AssistantConversationDetailResponse", description = "One AI conversation")
public record AssistantConversationDetailResponse(
    String id,
    String title,
    String createdAt,
    String updatedAt,
    List<AssistantMessageResponse> messages,
    boolean truncated) {}
