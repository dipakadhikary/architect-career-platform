package com.acos.knowledge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.auth.security.AcosUserDetails;
import com.acos.common.handler.GlobalExceptionHandler;
import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteRequest;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.service.KnowledgeService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** MockMvc slice tests for {@link KnowledgeController}. */
@WebMvcTest(controllers = KnowledgeController.class)
@Import({
  GlobalExceptionHandler.class,
  KnowledgeControllerTest.PermitAllSecurityConfiguration.class
})
class KnowledgeControllerTest {

  private static final String BASE_PATH = "/api/v1/knowledge/notes";
  private static final UUID USER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private static final UUID NOTE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final String EMAIL = "ada@acos.local";

  @Autowired private MockMvc mockMvc;
  @MockitoBean private KnowledgeService knowledgeService;

  @Test
  void shouldCreateNote() throws Exception {
    KnowledgeNoteResponse response = sampleNote();
    when(knowledgeService.create(eq(USER_ID), any(KnowledgeNoteRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            post(BASE_PATH)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"System Design Interview Notes",
                      "summary":"Key patterns",
                      "content":"## CAP Theorem",
                      "categoryName":"System Design",
                      "tagNames":["interview"]
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(NOTE_ID.toString()))
        .andExpect(jsonPath("$.data.title").value("System Design Interview Notes"));

    verify(knowledgeService).create(eq(USER_ID), any(KnowledgeNoteRequest.class));
  }

  @Test
  void shouldUpdateNote() throws Exception {
    when(knowledgeService.update(eq(USER_ID), eq(NOTE_ID), any(KnowledgeNoteRequest.class)))
        .thenReturn(sampleNote());

    mockMvc
        .perform(
            put(BASE_PATH + "/" + NOTE_ID)
                .with(authenticated())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"System Design Interview Notes",
                      "summary":"Key patterns",
                      "content":"## CAP Theorem"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(NOTE_ID.toString()));
  }

  @Test
  void shouldDeleteNote() throws Exception {
    mockMvc
        .perform(delete(BASE_PATH + "/" + NOTE_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(knowledgeService).delete(USER_ID, NOTE_ID);
  }

  @Test
  void shouldGetNote() throws Exception {
    when(knowledgeService.get(USER_ID, NOTE_ID)).thenReturn(sampleNote());

    mockMvc
        .perform(get(BASE_PATH + "/" + NOTE_ID).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").value("## CAP Theorem"));
  }

  @Test
  void shouldListNotes() throws Exception {
    when(knowledgeService.list(eq(USER_ID), any(Pageable.class)))
        .thenReturn(new KnowledgeNotePageResponse(List.of(sampleNote()), 0, 20, 1, 1, true, true));

    mockMvc
        .perform(get(BASE_PATH).with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(NOTE_ID.toString()));
  }

  @Test
  void shouldSearchNotes() throws Exception {
    when(knowledgeService.search(eq(USER_ID), eq("cap"), any(Pageable.class)))
        .thenReturn(new KnowledgeNotePageResponse(List.of(sampleNote()), 0, 20, 1, 1, true, true));

    mockMvc
        .perform(get(BASE_PATH + "/search").param("q", "cap").with(authenticated()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content[0].title").value("System Design Interview Notes"));
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticated() {
    AcosUserDetails principal =
        new AcosUserDetails(
            USER_ID, EMAIL, "hash", true, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    return authentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  private static KnowledgeNoteResponse sampleNote() {
    return new KnowledgeNoteResponse(
        NOTE_ID,
        "System Design Interview Notes",
        "Key patterns",
        "## CAP Theorem",
        null,
        List.of("interview"),
        Instant.parse("2026-08-04T06:00:00Z"),
        Instant.parse("2026-08-04T06:00:00Z"));
  }

  /** Permissive security for controller slice tests; JWT enforcement is covered by IT. */
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
