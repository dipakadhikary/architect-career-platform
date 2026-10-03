package com.acos.integration.controller;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import com.acos.integration.dto.AssistantChatRequest;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.gateway.AssistantAiGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authenticated pass-through to the Python Ask ACOS assistant. */
@RestController
@RequestMapping("/api/v1/integration/ai")
@Tag(name = "AI Integration", description = "Authenticated ACOS AI assistant")
@SecurityRequirement(name = "bearer-jwt")
public class AssistantAiController {

  private final AssistantAiGateway assistantAiGateway;

  /**
   * Creates the controller.
   *
   * @param assistantAiGateway assistant gateway
   */
  public AssistantAiController(AssistantAiGateway assistantAiGateway) {
    this.assistantAiGateway =
        Objects.requireNonNull(assistantAiGateway, "assistantAiGateway must not be null");
  }

  /**
   * Asks ACOS AI. The caller identity is the authenticated principal, not a body field.
   *
   * @param principal authenticated user
   * @param request message list
   * @return normalized answer
   */
  @PostMapping(
      path = "/chat",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Ask ACOS AI",
      description = "Returns a general AI answer. Does not search ACOS knowledge.")
  public ResponseEntity<ApiResponse<AssistantChatResponse>> chat(
      @AuthenticationPrincipal AcosUserDetails principal,
      @Valid @RequestBody AssistantChatRequest request) {
    if (principal == null || principal.getId() == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "Authentication required");
    }
    return ResponseEntity.ok(ApiResponse.success(assistantAiGateway.ask(request)));
  }
}
