package com.acos.integration.gateway;

import com.acos.integration.client.AssistantAiClient;
import com.acos.integration.dto.AuthoringEditRequest;
import com.acos.integration.dto.AuthoringProposalRequest;
import com.acos.integration.dto.AuthoringProposalResponse;
import com.acos.integration.dto.AuthoringRegenerateRequest;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.support.AiPlatformInvoker;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/** Forwards authoring calls. Disabled integration does not invent a draft. */
@Service
public class AuthoringAiGateway {

  private static final String FEATURE = "authoring";

  private final ObjectProvider<AssistantAiClient> client;
  private final AiPlatformInvoker invoker;

  /**
   * Creates the gateway.
   *
   * @param client optional Feign client
   * @param invoker resilience invoker
   */
  public AuthoringAiGateway(ObjectProvider<AssistantAiClient> client, AiPlatformInvoker invoker) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.invoker = Objects.requireNonNull(invoker, "invoker must not be null");
  }

  /**
   * Creates a draft.
   *
   * @param request authorized request
   * @return draft proposal
   */
  public AuthoringProposalResponse generate(AuthoringProposalRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        "generate",
        "/api/v1/ai/authoring/proposals",
        () -> requireClient().generateProposal(request),
        AuthoringAiGateway::disabled);
  }

  /**
   * Loads a draft.
   *
   * @param proposalId proposal id
   * @return draft proposal
   */
  public AuthoringProposalResponse get(String proposalId) {
    return invoker.execute(
        FEATURE,
        "get",
        "/api/v1/ai/authoring/proposals/{id}",
        () -> requireClient().getProposal(proposalId),
        AuthoringAiGateway::disabled);
  }

  /**
   * Edits draft text.
   *
   * @param proposalId proposal id
   * @param request edited markdown
   * @return updated draft
   */
  public AuthoringProposalResponse edit(String proposalId, AuthoringEditRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    return invoker.execute(
        FEATURE,
        "edit",
        "/api/v1/ai/authoring/proposals/{id}",
        () -> requireClient().editProposal(proposalId, request),
        AuthoringAiGateway::disabled);
  }

  /**
   * Regenerates a draft.
   *
   * @param proposalId previous proposal id
   * @param request optional instructions
   * @return new draft
   */
  public AuthoringProposalResponse regenerate(
      String proposalId, AuthoringRegenerateRequest request) {
    AuthoringRegenerateRequest body =
        request == null ? new AuthoringRegenerateRequest(null) : request;
    return invoker.execute(
        FEATURE,
        "regenerate",
        "/api/v1/ai/authoring/proposals/{id}/regenerate",
        () -> requireClient().regenerateProposal(proposalId, body),
        AuthoringAiGateway::disabled);
  }

  /**
   * Accepts a draft without publishing it.
   *
   * @param proposalId proposal id
   * @return accepted draft
   */
  public AuthoringProposalResponse accept(String proposalId) {
    return invoker.execute(
        FEATURE,
        "accept",
        "/api/v1/ai/authoring/proposals/{id}/accept",
        () -> requireClient().acceptProposal(proposalId),
        AuthoringAiGateway::disabled);
  }

  /**
   * Rejects a draft.
   *
   * @param proposalId proposal id
   * @return rejected draft
   */
  public AuthoringProposalResponse reject(String proposalId) {
    return invoker.execute(
        FEATURE,
        "reject",
        "/api/v1/ai/authoring/proposals/{id}/reject",
        () -> requireClient().rejectProposal(proposalId),
        AuthoringAiGateway::disabled);
  }

  private static <T> T disabled() {
    throw new AiPlatformUnavailableException("ACOS AI is disabled");
  }

  private AssistantAiClient requireClient() {
    AssistantAiClient resolved = client.getIfAvailable();
    if (resolved == null) {
      throw new AiPlatformUnavailableException("ACOS AI client is not available");
    }
    return resolved;
  }
}
