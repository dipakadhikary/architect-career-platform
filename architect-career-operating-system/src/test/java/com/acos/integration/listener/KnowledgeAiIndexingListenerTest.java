package com.acos.integration.listener;

import static org.mockito.Mockito.verify;

import com.acos.integration.dto.ContentIndexRequest;
import com.acos.integration.gateway.ContentIndexGateway;
import com.acos.knowledge.event.KnowledgeCreatedEvent;
import com.acos.knowledge.event.KnowledgeDeletedEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KnowledgeAiIndexingListenerTest {

  @Mock private ContentIndexGateway contentIndexGateway;

  @Test
  void shouldIndexCreatedNoteWithoutWaitingForTheCaller() {
    KnowledgeAiIndexingListener listener = new KnowledgeAiIndexingListener(contentIndexGateway);
    UUID noteId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    listener.onKnowledgeCreated(
        new KnowledgeCreatedEvent(
            noteId, ownerId, "Factory", "# Factory", List.of("pattern"), Instant.now(), 3));

    ArgumentCaptor<ContentIndexRequest> request =
        ArgumentCaptor.forClass(ContentIndexRequest.class);
    verify(contentIndexGateway).index(request.capture());
    ContentIndexRequest body = request.getValue();
    org.assertj.core.api.Assertions.assertThat(body.contentType()).isEqualTo("NOTE");
    org.assertj.core.api.Assertions.assertThat(body.ownerId()).isEqualTo(ownerId);
    org.assertj.core.api.Assertions.assertThat(body.contentVersion()).isEqualTo(3);
    org.assertj.core.api.Assertions.assertThat(body.sourceUrl()).isEqualTo("/knowledge/" + noteId);
  }

  @Test
  void shouldDeleteIndexedNote() {
    KnowledgeAiIndexingListener listener = new KnowledgeAiIndexingListener(contentIndexGateway);
    UUID noteId = UUID.randomUUID();
    listener.onKnowledgeDeleted(new KnowledgeDeletedEvent(noteId, UUID.randomUUID(), 4));
    verify(contentIndexGateway).delete(noteId);
  }
}
