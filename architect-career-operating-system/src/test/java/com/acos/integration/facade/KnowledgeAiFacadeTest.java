package com.acos.integration.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.integration.config.AiPlatformProperties;
import com.acos.integration.dto.KnowledgeIndexRequest;
import com.acos.integration.dto.KnowledgeIndexResponse;
import com.acos.integration.dto.KnowledgeSearchRequest;
import com.acos.integration.dto.KnowledgeSearchResponse;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.gateway.KnowledgeAiGateway;
import com.acos.integration.support.KnowledgeIndexingFailureHandler;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link KnowledgeAiFacade}. */
@ExtendWith(MockitoExtension.class)
class KnowledgeAiFacadeTest {

  private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID NOTE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private KnowledgeAiGateway knowledgeAiGateway;
  @Mock private KnowledgeIndexingFailureHandler knowledgeIndexingFailureHandler;

  private KnowledgeAiFacade facade;

  @BeforeEach
  void setUp() {
    facade =
        new KnowledgeAiFacade(
            knowledgeAiGateway,
            new AiFacadeSupport(disabledProperties()),
            knowledgeIndexingFailureHandler);
  }

  @Test
  void shouldReturnAcknowledgementWhenDisabled() {
    KnowledgeIndexRequest request =
        new KnowledgeIndexRequest(USER_ID, NOTE_ID, "Title", "Content", List.of("tag"));

    KnowledgeIndexResponse response = facade.indexKnowledge(request);

    assertThat(response.status()).isEqualTo("ACKNOWLEDGED");
    assertThat(response.noteId()).isEqualTo(NOTE_ID);
    verify(knowledgeAiGateway, never()).indexKnowledge(any());
  }

  @Test
  void shouldDelegateWhenEnabled() {
    facade =
        new KnowledgeAiFacade(
            knowledgeAiGateway,
            new AiFacadeSupport(enabledProperties()),
            knowledgeIndexingFailureHandler);
    KnowledgeIndexRequest request =
        new KnowledgeIndexRequest(USER_ID, NOTE_ID, "Title", "Content", List.of());
    KnowledgeIndexResponse expected =
        new KnowledgeIndexResponse(UUID.randomUUID(), NOTE_ID, "INDEXED", Instant.now());
    when(knowledgeAiGateway.indexKnowledge(request)).thenReturn(expected);

    assertThat(facade.indexKnowledge(request)).isEqualTo(expected);
  }

  @Test
  void shouldFallbackAndNotifyHandlerWhenLiveCallFails() {
    facade =
        new KnowledgeAiFacade(
            knowledgeAiGateway,
            new AiFacadeSupport(enabledProperties()),
            knowledgeIndexingFailureHandler);
    KnowledgeIndexRequest request =
        new KnowledgeIndexRequest(USER_ID, NOTE_ID, "Title", "Content", List.of());
    when(knowledgeAiGateway.indexKnowledge(request))
        .thenThrow(new AiPlatformUnavailableException("down"));

    KnowledgeIndexResponse response = facade.indexKnowledge(request);

    assertThat(response.status()).isEqualTo("ACKNOWLEDGED");
    verify(knowledgeIndexingFailureHandler).handle(any(), any());
  }

  @Test
  void shouldReturnEmptySearchWhenDisabled() {
    KnowledgeSearchRequest request = new KnowledgeSearchRequest(USER_ID, "query", 5);

    KnowledgeSearchResponse response = facade.searchKnowledge(request);

    assertThat(response.hits()).isEmpty();
    verify(knowledgeAiGateway, never()).searchKnowledge(any());
  }

  @Test
  void shouldRejectBlankSearchQuery() {
    assertThatThrownBy(() -> facade.searchKnowledge(new KnowledgeSearchRequest(USER_ID, "  ", 5)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("query");
  }

  private static AiPlatformProperties disabledProperties() {
    return properties(false);
  }

  private static AiPlatformProperties enabledProperties() {
    return properties(true);
  }

  private static AiPlatformProperties properties(boolean enabled) {
    return new AiPlatformProperties(
        enabled,
        "http://localhost:8090",
        "key",
        Duration.ofSeconds(3),
        Duration.ofSeconds(30),
        "BASIC",
        new AiPlatformProperties.CompressionProperties(true, true, 2048),
        new AiPlatformProperties.ResilienceProperties("ai-platform"),
        new AiPlatformProperties.RetryProperties(
            true, 3, Duration.ofMillis(200), Duration.ofSeconds(2)));
  }
}
