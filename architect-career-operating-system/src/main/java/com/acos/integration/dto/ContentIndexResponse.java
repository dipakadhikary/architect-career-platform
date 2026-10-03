package com.acos.integration.dto;

/**
 * Indexing acknowledgement from the Python service.
 *
 * @param contentId source content id
 * @param status indexing status
 * @param chunkCount vectors written for the current version
 * @param contentVersion indexed version
 * @param embeddingModel embedding model name
 * @param embeddingProvider embedding provider name
 * @param indexVersion index schema version
 * @param retryable whether a later attempt can succeed
 * @param error error type when status is FAILED
 */
public record ContentIndexResponse(
    String contentId,
    String status,
    int chunkCount,
    long contentVersion,
    String embeddingModel,
    String embeddingProvider,
    int indexVersion,
    boolean retryable,
    String error) {}
