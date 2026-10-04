package com.acos.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.integration.dto.AssistantChatResponse;
import com.acos.integration.dto.AssistantConversationResponse;
import com.acos.integration.dto.AssistantMessagePairResponse;
import com.acos.integration.dto.AssistantMessageRequest;
import com.acos.integration.dto.AssistantMessageResponse;
import com.acos.integration.gateway.AssistantAiGateway;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** MockMvc tests for the Ask ACOS pass-through. */
@WebMvcTest(controllers = AssistantAiController.class)
@Import({
  GlobalExceptionHandler.class,
  AssistantAiControllerTest.PermitAllSecurityConfiguration.class
})
class AssistantAiControllerTest {

  private static final String PATH = "/api/v1/integration/ai/chat";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AssistantAiGateway assistantAiGateway;

  @Test
  void shouldReturnNormalizedAnswerForAuthenticatedCaller() throws Exception {
    when(assistantAiGateway.ask(any()))
        .thenReturn(
            new AssistantChatResponse(
                "## Dependency Injection", "gpt-test", "openai", false, java.util.List.of()));

    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"messages\":[{\"role\":\"user\",\"content\":\"Explain dependency"
                        + " injection\"}]}")
                .with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.answer").value("## Dependency Injection"))
        .andExpect(jsonPath("$.data.provider").value("openai"));
  }

  @Test
  void shouldRejectMissingAuthentication() throws Exception {
    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"messages\":[{\"role\":\"user\",\"content\":\"Explain dependency"
                        + " injection\"}]}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldCreateConversationForAuthenticatedCaller() throws Exception {
    when(assistantAiGateway.createConversation())
        .thenReturn(
            new AssistantConversationResponse(
                "conv-1", "New conversation", "2026-10-04T00:00:00Z", "2026-10-04T00:00:00Z"));

    mockMvc
        .perform(post("/api/v1/integration/ai/conversations").with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value("conv-1"))
        .andExpect(jsonPath("$.data.title").value("New conversation"));
  }

  @Test
  void shouldRejectConversationCreateWithoutAuthentication() throws Exception {
    mockMvc
        .perform(post("/api/v1/integration/ai/conversations"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldForwardIdempotencyKeyWhenSendingAMessage() throws Exception {
    when(assistantAiGateway.sendMessage(
            eq("conv-1"), any(AssistantMessageRequest.class), eq("key-1")))
        .thenReturn(new AssistantMessagePairResponse(message("user"), message("assistant")));

    mockMvc
        .perform(
            post("/api/v1/integration/ai/conversations/conv-1/messages")
                .contentType(APPLICATION_JSON)
                .header("Idempotency-Key", "key-1")
                .content("{\"content\":\"What is Kafka?\"}")
                .with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.userMessage.content").value("What is Kafka?"));

    verify(assistantAiGateway)
        .sendMessage(eq("conv-1"), any(AssistantMessageRequest.class), eq("key-1"));
  }

  @Test
  void shouldDeleteConversationForAuthenticatedCaller() throws Exception {
    mockMvc
        .perform(delete("/api/v1/integration/ai/conversations/conv-1").with(authenticated()))
        .andExpect(status().isNoContent());

    verify(assistantAiGateway).deleteConversation("conv-1");
  }

  @Test
  void shouldRejectEmptyMessages() throws Exception {
    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content("{\"messages\":[]}")
                .with(authenticated()))
        .andExpect(status().isBadRequest());
  }

  private static AssistantMessageResponse message(String role) {
    return new AssistantMessageResponse(
        role + "-1",
        role,
        "What is Kafka?",
        1,
        "COMPLETED",
        "2026-10-04T00:00:00Z",
        "",
        "",
        false,
        List.of());
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID,
            "user@example.com",
            "hash",
            true,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  /** Permissive security for controller slice tests. */
  static class PermitAllSecurityConfiguration {

    @Bean
    SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .sessionManagement(
              session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
      return http.build();
    }
  }
}
