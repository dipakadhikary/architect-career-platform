package com.acos.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.integration.dto.AuthoringProposalRequest;
import com.acos.integration.dto.AuthoringProposalResponse;
import com.acos.integration.gateway.AuthoringAiGateway;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.exception.KnowledgeNoteNotFoundException;
import com.acos.knowledge.service.KnowledgeService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

/** MockMvc tests for AI draft generation. Knowledge is not saved by these endpoints. */
@WebMvcTest(controllers = AuthoringAiController.class)
@Import({
  GlobalExceptionHandler.class,
  AuthoringAiControllerTest.PermitAllSecurityConfiguration.class
})
class AuthoringAiControllerTest {

  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID NOTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthoringAiGateway authoringAiGateway;

  @MockitoBean private KnowledgeService knowledgeService;

  @Test
  void shouldUseTheOwnedNoteInsteadOfClientContent() throws Exception {
    when(knowledgeService.get(USER_ID, NOTE_ID))
        .thenReturn(
            new KnowledgeNoteResponse(
                NOTE_ID,
                "Kafka",
                "Summary",
                "Authoritative note",
                null,
                List.of(),
                Instant.parse("2026-10-04T00:00:00Z"),
                Instant.parse("2026-10-04T00:00:00Z"),
                11L));
    when(authoringAiGateway.generate(any())).thenReturn(draft("GENERATED"));

    mockMvc
        .perform(
            post("/api/v1/integration/ai/authoring/proposals")
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"operation\":\"IMPROVE\",\"contentId\":\""
                        + NOTE_ID
                        + "\",\"sourceContent\":\"forged\"}")
                .with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.authoritative").value(false))
        .andExpect(jsonPath("$.data.status").value("GENERATED"));

    ArgumentCaptor<AuthoringProposalRequest> captor =
        ArgumentCaptor.forClass(AuthoringProposalRequest.class);
    verify(authoringAiGateway).generate(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue().sourceContent())
        .isEqualTo("Authoritative note");
    org.assertj.core.api.Assertions.assertThat(captor.getValue().sourceVersion()).isEqualTo(11L);
    verify(knowledgeService, never()).update(any(), any(), any());
  }

  @Test
  void shouldRejectAnotherUsersNoteBeforeCallingTheModel() throws Exception {
    when(knowledgeService.get(USER_ID, NOTE_ID))
        .thenThrow(new KnowledgeNoteNotFoundException(NOTE_ID));

    mockMvc
        .perform(
            post("/api/v1/integration/ai/authoring/proposals")
                .contentType(APPLICATION_JSON)
                .content("{\"operation\":\"IMPROVE\",\"contentId\":\"" + NOTE_ID + "\"}")
                .with(authenticated()))
        .andExpect(status().isNotFound());

    verify(authoringAiGateway, never()).generate(any());
  }

  @Test
  void shouldRequireAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/integration/ai/authoring/proposals")
                .contentType(APPLICATION_JSON)
                .content("{\"operation\":\"GENERATE\",\"topic\":\"Kafka\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAcceptADraftWithoutUpdatingKnowledge() throws Exception {
    when(authoringAiGateway.accept("proposal-1")).thenReturn(draft("APPROVED"));

    mockMvc
        .perform(
            post("/api/v1/integration/ai/authoring/proposals/proposal-1/accept")
                .with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("APPROVED"))
        .andExpect(jsonPath("$.data.authoritative").value(false));

    verify(knowledgeService, never()).update(any(), any(), any());
    verify(knowledgeService, never()).create(any(), any());
  }

  private static AuthoringProposalResponse draft(String status) {
    return new AuthoringProposalResponse(
        "proposal-1",
        "IMPROVE",
        status,
        "## Draft",
        List.of(),
        List.of(),
        List.of(),
        "fake",
        "fake",
        "v1",
        false,
        false,
        NOTE_ID.toString(),
        11L);
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
