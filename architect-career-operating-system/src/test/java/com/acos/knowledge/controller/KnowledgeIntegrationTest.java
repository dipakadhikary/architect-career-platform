package com.acos.knowledge.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.testsupport.AuthApiTestSupport;
import com.acos.testsupport.PostgresTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

/** End-to-end integration tests for the authenticated knowledge API. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KnowledgeIntegrationTest {

  private static final String NOTES_PATH = "/api/v1/knowledge/notes";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldSupportFullKnowledgeNoteLifecycle() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("knowledge");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    MvcResult createResult =
        mockMvc
            .perform(
                post(NOTES_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"CAP Theorem Notes",
                          "summary":"Distributed systems trade-offs",
                          "content":"## Consistency\\n\\nPrefer availability carefully.",
                          "categoryName":"System Design",
                          "tagNames":["interview", "distributed"]
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value("CAP Theorem Notes"))
            .andExpect(jsonPath("$.data.category.name").value("System Design"))
            .andExpect(jsonPath("$.data.tags[0]").value("distributed"))
            .andExpect(jsonPath("$.data.tags[1]").value("interview"))
            .andReturn();

    String noteId =
        objectMapper
            .readTree(createResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    mockMvc
        .perform(get(NOTES_PATH + "/" + noteId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.summary").value("Distributed systems trade-offs"));

    mockMvc
        .perform(
            put(NOTES_PATH + "/" + noteId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"CAP Theorem Notes Updated",
                      "summary":"Updated summary about partitions",
                      "content":"## Updated\\n\\nPartition tolerance first.",
                      "categoryName":"Interviews",
                      "tagNames":["cap"]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("CAP Theorem Notes Updated"))
        .andExpect(jsonPath("$.data.category.name").value("Interviews"))
        .andExpect(jsonPath("$.data.tags[0]").value("cap"));

    mockMvc
        .perform(get(NOTES_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(noteId));

    mockMvc
        .perform(
            get(NOTES_PATH + "/search")
                .param("q", "partitions")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].id").value(noteId));

    mockMvc
        .perform(delete(NOTES_PATH + "/" + noteId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    mockMvc
        .perform(get(NOTES_PATH + "/" + noteId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  void shouldRejectKnowledgeApisWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(NOTES_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldRejectBlankSearchQuery() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("knowledge-search");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    mockMvc
        .perform(
            get(NOTES_PATH + "/search")
                .param("q", "   ")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }
}
