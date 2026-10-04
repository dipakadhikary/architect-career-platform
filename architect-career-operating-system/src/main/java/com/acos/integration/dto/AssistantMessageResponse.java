package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * One user or assistant turn. Sources come from retrieval metadata.
 *
 * @param id message id
 * @param role USER or ASSISTANT
 * @param content message text
 * @param sequenceNumber order within the conversation
 * @param status COMPLETED, PROCESSING, or FAILED
 * @param createdAt creation time
 * @param model model name for an assistant reply
 * @param provider provider name for an assistant reply
 * @param grounded whether the reply used ACOS knowledge
 * @param sources trusted source snapshots
 */
@Schema(name = "AssistantMessageResponse", description = "One conversation message")
public record AssistantMessageResponse(
    String id,
    String role,
    String content,
    int sequenceNumber,
    String status,
    String createdAt,
    String model,
    String provider,
    boolean grounded,
    List<AssistantSource> sources) {}
