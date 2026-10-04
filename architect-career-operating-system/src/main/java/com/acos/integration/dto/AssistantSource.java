package com.acos.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A source opened from ACOS. The URL comes from indexed metadata.
 *
 * @param contentId source content id
 * @param topicId tutorial topic id when present
 * @param title source title
 * @param contentType NOTE, CONCEPT, or QUESTIONS_ANSWERS
 * @param section section heading
 * @param path breadcrumb or topic path
 * @param url ACOS path for the source
 * @param chunkId retrieved chunk id
 * @param score similarity score
 */
@Schema(name = "AssistantSource", description = "ACOS knowledge source for an answer")
public record AssistantSource(
    String contentId,
    String topicId,
    String title,
    String contentType,
    String section,
    String path,
    String url,
    String chunkId,
    double score) {}
