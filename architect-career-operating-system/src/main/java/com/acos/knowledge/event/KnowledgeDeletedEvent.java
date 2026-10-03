package com.acos.knowledge.event;

import java.util.Objects;
import java.util.UUID;

/**
 * Published after a knowledge note is deleted and the business transaction commits.
 *
 * @param noteId note id
 * @param ownerId owning user id
 * @param version optimistic version captured before delete
 */
public record KnowledgeDeletedEvent(UUID noteId, UUID ownerId, long version) {

  /**
   * Validates required fields.
   *
   * @param noteId note id
   * @param ownerId owner id
   * @param version version
   */
  public KnowledgeDeletedEvent {
    Objects.requireNonNull(noteId, "noteId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
  }
}
