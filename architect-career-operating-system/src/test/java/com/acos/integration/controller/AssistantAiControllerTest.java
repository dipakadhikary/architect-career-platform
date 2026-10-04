package com.acos.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.integration.dto.AssistantChatResponse;
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
  void shouldRejectEmptyMessages() throws Exception {
    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content("{\"messages\":[]}")
                .with(authenticated()))
        .andExpect(status().isBadRequest());
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
