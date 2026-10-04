package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * AI draft. {@code authoritative} is always false. Accepting it does not publish knowledge.
 *
 * @param proposalId proposal id, distinct from a knowledge note id
 * @param operation authoring operation
 * @param status GENERATED, EDITING, APPROVED, or REJECTED
 * @param content markdown draft
 * @param questions structured questions when the operation produced them
 * @param sources retrieval metadata, never model-invented URLs
 * @param warnings grounding or truncation warnings
 * @param model model name
 * @param provider provider name
 * @param promptVersion prompt template version
 * @param grounded whether retrieved ACOS knowledge was used
 * @param authoritative always false
 * @param contentId source note id when one was supplied
 * @param sourceVersion note version captured when generation started
 */
@Schema(name = "AuthoringProposalResponse", description = "AI-generated knowledge draft")
public record AuthoringProposalResponse(
    String proposalId,
    String operation,
    String status,
    String content,
    List<AuthoringQuestionResponse> questions,
    List<AssistantSource> sources,
    List<String> warnings,
    String model,
    String provider,
    String promptVersion,
    boolean grounded,
    boolean authoritative,
    String contentId,
    Long sourceVersion) {}
