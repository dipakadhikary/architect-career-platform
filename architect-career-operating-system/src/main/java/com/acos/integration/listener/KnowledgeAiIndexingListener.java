package com.acos.integration.listener;

import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.facade.KnowledgeAiFacade;
import com.acos.knowledge.event.KnowledgeCreatedEvent;
import com.acos.knowledge.event.KnowledgeUpdatedEvent;
import java.util.Objects;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Asynchronously indexes knowledge notes after successful business commits. AI failures never
 * affect the originating business transaction; the facade returns an acknowledgement fallback.
 */
@Component
public class KnowledgeAiIndexingListener {

  private final KnowledgeAiFacade knowledgeAiFacade;

  /**
   * Creates the listener.
   *
   * @param knowledgeAiFacade knowledge AI facade
   */
  public KnowledgeAiIndexingListener(KnowledgeAiFacade knowledgeAiFacade) {
    this.knowledgeAiFacade =
        Objects.requireNonNull(knowledgeAiFacade, "knowledgeAiFacade must not be null");
  }

  /**
   * Indexes a newly created knowledge note after commit.
   *
   * @param event created event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onKnowledgeCreated(KnowledgeCreatedEvent event) {
    index(
        new KnowledgeIndexRequest(
            event.ownerId(), event.noteId(), event.title(), event.content(), event.tags()));
  }

  /**
   * Indexes an updated knowledge note after commit.
   *
   * @param event updated event
   */
  @Async("aiTaskExecutor")
  @EventListener
  public void onKnowledgeUpdated(KnowledgeUpdatedEvent event) {
    index(
        new KnowledgeIndexRequest(
            event.ownerId(), event.noteId(), event.title(), event.content(), event.tags()));
  }

  private void index(KnowledgeIndexRequest request) {
    knowledgeAiFacade.indexKnowledge(request);
  }
}
