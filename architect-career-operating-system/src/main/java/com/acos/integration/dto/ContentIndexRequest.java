package com.acos.integration.dto;

import java.util.UUID;

/**
 * Trusted indexing payload. The caller is ACOS, not an anonymous browser.
 *
 * @param contentId source content id
 * @param ownerId owning user id
 * @param contentType NOTE, CONCEPT, or QUESTIONS_ANSWERS
 * @param title display title
 * @param content markdown body for notes and concepts
 * @param question question text
 * @param answer answer markdown
 * @param contentVersion ACOS optimistic version
 * @param topicId tutorial topic id
 * @param slug topic slug
 * @param path topic path
 * @param sourceUrl path that opens the source in ACOS
 */
public record ContentIndexRequest(
    UUID contentId,
    UUID ownerId,
    String contentType,
    String title,
    String content,
    String question,
    String answer,
    long contentVersion,
    UUID topicId,
    String slug,
    String path,
    String sourceUrl) {}
