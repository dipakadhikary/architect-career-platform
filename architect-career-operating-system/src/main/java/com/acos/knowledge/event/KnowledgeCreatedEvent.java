package com.acos.knowledge.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Published after a knowledge note is created and the business transaction commits.
 *
 * @param noteId note id
 * @param ownerId owning user id
 * @param title note title
 * @param content note content
 * @param tags note tags
 * @param occurredAt event time
 * @param version optimistic version captured from the note
 */
public record KnowledgeCreatedEvent(
    UUID noteId,
    UUID ownerId,
    String title,
    String content,
    List<String> tags,
    Instant occurredAt,
    long version) {

  /**
   * Validates required fields.
   *
   * @param noteId note id
   * @param ownerId owner id
   * @param title title
   * @param content content
   * @param tags tags
   * @param occurredAt occurred at
   * @param version version
   */
  public KnowledgeCreatedEvent {
    Objects.requireNonNull(noteId, "noteId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(title, "title must not be null");
    Objects.requireNonNull(content, "content must not be null");
    Objects.requireNonNull(tags, "tags must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    tags = List.copyOf(tags);
  }
}
