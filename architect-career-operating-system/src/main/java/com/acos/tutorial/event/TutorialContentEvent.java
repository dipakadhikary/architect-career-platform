package com.acos.tutorial.event;

import java.util.Objects;
import java.util.UUID;

/**
 * Published after a tutorial concept or question changes and the transaction commits.
 *
 * @param contentId concept or question id
 * @param ownerId owning user id
 * @param topicId topic id
 * @param contentType CONCEPT or QUESTIONS_ANSWERS
 * @param title topic title
 * @param content concept markdown, empty for questions
 * @param question question text
 * @param answer answer markdown
 * @param slug topic slug
 * @param path topic path
 * @param sourceUrl ACOS path that opens the source
 * @param version optimistic version
 * @param deleted whether the source row was removed
 */
public record TutorialContentEvent(
    UUID contentId,
    UUID ownerId,
    UUID topicId,
    String contentType,
    String title,
    String content,
    String question,
    String answer,
    String slug,
    String path,
    String sourceUrl,
    long version,
    boolean deleted) {

  /**
   * Validates required fields.
   *
   * @param contentId content id
   * @param ownerId owner id
   * @param topicId topic id
   * @param contentType content type
   * @param title title
   * @param content concept body
   * @param question question text
   * @param answer answer text
   * @param slug slug
   * @param path path
   * @param sourceUrl source url
   * @param version version
   * @param deleted deleted flag
   */
  public TutorialContentEvent {
    Objects.requireNonNull(contentId, "contentId must not be null");
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(contentType, "contentType must not be null");
    Objects.requireNonNull(title, "title must not be null");
    content = content == null ? "" : content;
    question = question == null ? "" : question;
    answer = answer == null ? "" : answer;
    slug = slug == null ? "" : slug;
    path = path == null ? "" : path;
    sourceUrl = sourceUrl == null ? "" : sourceUrl;
  }
}
