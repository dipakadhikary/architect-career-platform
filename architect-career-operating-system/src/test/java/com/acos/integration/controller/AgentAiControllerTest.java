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
import com.acos.integration.dto.AgentExecutionResponse;
import com.acos.integration.dto.AgentStepResponse;
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

/** MockMvc tests for the controlled agent pass-through. */
@WebMvcTest(controllers = AgentAiController.class)
@Import({GlobalExceptionHandler.class, AgentAiControllerTest.PermitAllSecurityConfiguration.class})
class AgentAiControllerTest {

  private static final String PATH = "/api/v1/integration/ai/agents/execute";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AssistantAiGateway assistantAiGateway;

  @Test
  void shouldReturnTheAgentExecutionForAnAuthenticatedCaller() throws Exception {
    when(assistantAiGateway.executeAgent(any()))
        .thenReturn(
            new AgentExecutionResponse(
                "exec-1",
                "COMPLETED",
                "Kafka transactions append atomically.",
                "",
                List.of(),
                List.of(
                    new AgentStepResponse(
                        "step-1", "search_knowledge", "Searching ACOS knowledge", "COMPLETED")),
                false,
                ""));

    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content("{\"goal\":\"Find Kafka transactions\"}")
                .with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.executionId").value("exec-1"))
        .andExpect(jsonPath("$.data.steps[0].label").value("Searching ACOS knowledge"));
  }

  @Test
  void shouldRejectMissingAuthentication() throws Exception {
    mockMvc
        .perform(
            post(PATH)
                .contentType(APPLICATION_JSON)
                .content("{\"goal\":\"Find Kafka transactions\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
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
