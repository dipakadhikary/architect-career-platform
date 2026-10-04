package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * One page of the caller's conversations, newest update first.
 *
 * @param content conversations on this page
 * @param page zero-based page index
 * @param size page size
 * @param totalElements total conversations for this caller
 * @param totalPages total pages
 * @param first whether this is the first page
 * @param last whether this is the last page
 */
@Schema(name = "AssistantConversationPageResponse", description = "Paged AI conversations")
public record AssistantConversationPageResponse(
    List<AssistantConversationResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {}
