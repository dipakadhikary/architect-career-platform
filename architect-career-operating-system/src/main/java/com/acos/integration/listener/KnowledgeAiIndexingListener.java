package com.acos.integration.listener;

import com.acos.integration.dto.ContentIndexRequest;
import com.acos.integration.gateway.ContentIndexGateway;
import com.acos.knowledge.event.KnowledgeCreatedEvent;
import com.acos.knowledge.event.KnowledgeDeletedEvent;
import com.acos.knowledge.event.KnowledgeUpdatedEvent;
import com.acos.tutorial.event.TutorialContentEvent;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Indexes ACOS content after the business transaction commits. Embedding does not run inside the
 * user request. Failures here do not roll back the note or tutorial write.
 */
@Component
public class KnowledgeAiIndexingListener {

  private final ContentIndexGateway contentIndexGateway;

  /**
   * Creates the listener.
   *
   * @param contentIndexGateway index gateway
   */
  public KnowledgeAiIndexingListener(ContentIndexGateway contentIndexGateway) {
    this.contentIndexGateway =
        Objects.requireNonNull(contentIndexGateway, "contentIndexGateway must not be null");
  }

  /**
   * Indexes a newly created knowledge note.
   *
   * @param event created event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onKnowledgeCreated(KnowledgeCreatedEvent event) {
    contentIndexGateway.index(
        noteRequest(
            event.noteId(), event.ownerId(), event.title(), event.content(), event.version()));
  }

  /**
   * Replaces the index for an updated knowledge note.
   *
   * @param event updated event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onKnowledgeUpdated(KnowledgeUpdatedEvent event) {
    contentIndexGateway.index(
        noteRequest(
            event.noteId(), event.ownerId(), event.title(), event.content(), event.version()));
  }

  /**
   * Removes vectors for a deleted knowledge note.
   *
   * @param event deleted event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onKnowledgeDeleted(KnowledgeDeletedEvent event) {
    contentIndexGateway.delete(event.noteId());
  }

  /**
   * Indexes or removes a tutorial concept or question.
   *
   * @param event tutorial content event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onTutorialContent(TutorialContentEvent event) {
    if (event.deleted()) {
      contentIndexGateway.delete(event.contentId());
      return;
    }
    contentIndexGateway.index(
        new ContentIndexRequest(
            event.contentId(),
            event.ownerId(),
            event.contentType(),
            event.title(),
            event.content(),
            event.question(),
            event.answer(),
            event.version(),
            event.topicId(),
            event.slug(),
            event.path(),
            event.sourceUrl()));
  }

  private static ContentIndexRequest noteRequest(
      UUID noteId, UUID ownerId, String title, String content, long version) {
    return new ContentIndexRequest(
        noteId,
        ownerId,
        "NOTE",
        title,
        content,
        "",
        "",
        version,
        null,
        null,
        null,
        "/knowledge/" + noteId);
  }
}
