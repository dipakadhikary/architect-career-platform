package com.acos.integration.gateway;

import com.acos.integration.client.ContentIndexClient;
import com.acos.integration.dto.ContentIndexRequest;
import com.acos.integration.dto.ContentIndexResponse;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.support.AiPlatformInvoker;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Sends index and delete calls to Python. When the integration is disabled the call is skipped so
 * ACOS writes are unaffected.
 */
@Service
public class ContentIndexGateway {

  private static final String FEATURE = "index";

  private final ObjectProvider<ContentIndexClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client
   * @param invoker resilience invoker
   */
  public ContentIndexGateway(ObjectProvider<ContentIndexClient> client, AiPlatformInvoker invoker) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.invoker = Objects.requireNonNull(invoker, "invoker must not be null");
  }

  /**
   * Indexes one document.
   *
   * @param request trusted payload
   * @return acknowledgement, or SKIPPED when AI integration is disabled
   */
  public ContentIndexResponse index(ContentIndexRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        "upsert",
        "/api/v1/ai/index/content",
        () -> requireClient().index(request),
        () -> skipped(request.contentId()));
  }

  /**
   * Deletes vectors for one content id.
   *
   * @param contentId source content id
   * @return acknowledgement, or SKIPPED when AI integration is disabled
   */
  public ContentIndexResponse delete(UUID contentId) {
    Objects.requireNonNull(contentId, "contentId must not be null");
    return invoker.execute(
        FEATURE,
        "delete",
        "/api/v1/ai/index/content/" + contentId,
        () -> requireClient().delete(contentId),
        () -> skipped(contentId));
  }

  private ContentIndexClient requireClient() {
    ContentIndexClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new AiPlatformUnavailableException("Content index client is not available");
    }
    return resolved;
  }

  private static ContentIndexResponse skipped(UUID contentId) {
    return new ContentIndexResponse(contentId.toString(), "SKIPPED", 0, 0, "", "", 0, false, null);
  }
}
