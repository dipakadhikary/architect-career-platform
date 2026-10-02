package com.acos.sdk;

import com.acos.sdk.generated.ApiClient;
import com.acos.sdk.generated.api.KnowledgeApi;
import com.acos.sdk.generated.model.KnowledgeDeleteApiResponse;
import com.acos.sdk.generated.model.KnowledgeNoteApiResponse;
import com.acos.sdk.generated.model.KnowledgeNotePageApiResponse;
import com.acos.sdk.generated.model.KnowledgeNoteRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain wrapper around the generated {@link KnowledgeApi}.
 */
public final class KnowledgeApiService extends ServiceSupport {

  private final KnowledgeApi api;

  public KnowledgeApiService(ApiClient apiClient) {
    this(apiClient, RetryPolicy.defaults());
  }

  public KnowledgeApiService(ApiClient apiClient, RetryPolicy retryPolicy) {
    super(retryPolicy, KnowledgeApiService.class);
    this.api = new KnowledgeApi(Objects.requireNonNull(apiClient, "apiClient"));
  }

  public KnowledgeApi raw() {
    return api;
  }

  public KnowledgeNoteApiResponse createNote(KnowledgeNoteRequest request) {
    return execute("knowledge.createNote", () -> api.create8(request));
  }

  public KnowledgeNoteApiResponse updateNote(UUID noteId, KnowledgeNoteRequest request) {
    return execute("knowledge.updateNote", () -> api.update8(noteId, request));
  }

  public KnowledgeDeleteApiResponse deleteNote(UUID noteId) {
    return execute("knowledge.deleteNote", () -> api.delete8(noteId));
  }

  public KnowledgeNoteApiResponse getNote(UUID noteId) {
    return execute("knowledge.getNote", () -> api.get7(noteId));
  }

  public KnowledgeNotePageApiResponse listNotes(Integer page, Integer size, List<String> sort) {
    return execute("knowledge.listNotes", () -> api.list8(page, size, sort));
  }

  public KnowledgeNotePageApiResponse searchNotes(
      String q, Integer page, Integer size, List<String> sort) {
    return execute("knowledge.searchNotes", () -> api.search1(q, page, size, sort));
  }
}
