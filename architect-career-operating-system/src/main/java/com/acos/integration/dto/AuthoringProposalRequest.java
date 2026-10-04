package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * Authoring request. A content id is resolved by ACOS before the AI service sees any note text.
 *
 * @param operation authoring operation
 * @param topic optional topic
 * @param instructions optional user instructions
 * @param sourceContent optional markdown; ignored when {@code contentId} is set
 * @param contentId optional owned knowledge note
 * @param sourceVersion ignored when {@code contentId} is set; ACOS supplies the version
 * @param difficulty optional BEGINNER, INTERMEDIATE, or ADVANCED
 * @param questionCount optional question limit
 * @param conversationId optional Phase 6 conversation
 * @param useConversation include bounded conversation context only when true
 * @param useKnowledge retrieve ACOS knowledge when true
 */
@Schema(name = "AuthoringProposalRequest", description = "Create an AI content draft")
public record AuthoringProposalRequest(
    @NotBlank String operation,
    String topic,
    String instructions,
    String sourceContent,
    UUID contentId,
    Long sourceVersion,
    String difficulty,
    @Min(1) @Max(20) Integer questionCount,
    String conversationId,
    Boolean useConversation,
    Boolean useKnowledge) {}
