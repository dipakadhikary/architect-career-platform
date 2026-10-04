package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The stored user turn and the assistant reply for one send.
 *
 * @param userMessage persisted user message
 * @param assistantMessage persisted assistant message
 */
@Schema(name = "AssistantMessagePairResponse", description = "User and assistant messages")
public record AssistantMessagePairResponse(
    AssistantMessageResponse userMessage, AssistantMessageResponse assistantMessage) {}
