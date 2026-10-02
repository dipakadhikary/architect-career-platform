package com.acos.tutorial.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acos.testsupport.AuthApiTestSupport;
import com.acos.testsupport.PostgresTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TutorialIntegrationTest {

  private static final String BASE = "/api/v1/tutorials";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldSupportTutorialHierarchyConceptQuestionsAndSearch() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("tutorial");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    MvcResult rootResult =
        mockMvc
            .perform(
                post(BASE + "/topics")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
{"title":"Microservices Design","slug":null,"parentId":null,"sortOrder":null}
"""))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.path").value("microservices-design"))
            .andReturn();

    String rootId = readId(rootResult);

    MvcResult childResult =
        mockMvc
            .perform(
                post(BASE + "/topics")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {"title":"Bulkhead","slug":null,"parentId":"%s","sortOrder":null}
                        """
                            .formatted(rootId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.path").value("microservices-design/bulkhead"))
            .andReturn();

    String childId = readId(childResult);

    mockMvc
        .perform(
            put(BASE + "/topics/" + childId + "/concept")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
{"content":"The bulkhead pattern isolates failures between different components."}
"""))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.content").value(org.hamcrest.Matchers.containsString("bulkhead")));

    mockMvc
        .perform(
            post(BASE + "/topics/" + childId + "/questions")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "question":"What happens when a circuit breaker opens?",
                      "answer":"Calls fail fast until the circuit half-opens.",
                      "sortOrder":null
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.data.question").value(org.hamcrest.Matchers.containsString("circuit")));

    mockMvc
        .perform(get(BASE + "/tree").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].title").value("Microservices Design"))
        .andExpect(jsonPath("$.data[0].children[0].title").value("Bulkhead"))
        .andExpect(jsonPath("$.data[0].children[0].hasConcept").value(true))
        .andExpect(jsonPath("$.data[0].children[0].hasQuestions").value(true));

    mockMvc
        .perform(
            get(BASE + "/topics/by-path/concept")
                .param("path", "microservices-design/bulkhead")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.breadcrumb[0].title").value("Microservices Design"))
        .andExpect(jsonPath("$.data.breadcrumb[1].title").value("Bulkhead"));

    mockMvc
        .perform(
            get(BASE + "/topics/by-path/questions")
                .param("path", "microservices-design/bulkhead")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.questions.length()").value(1));

    mockMvc
        .perform(
            get(BASE + "/search")
                .param("q", "bulkhead isolates")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.content[0].contentType").value("Concept"))
        .andExpect(jsonPath("$.data.content[0].snippet").isNotEmpty());

    mockMvc
        .perform(
            get(BASE + "/search")
                .param("q", "circuit breaker")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.content[0].contentType").value("Questions & Answers"));

    mockMvc
        .perform(
            get(BASE + "/search")
                .param("q", "zzznomatchxyz")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(0));

    mockMvc
        .perform(
            put(BASE + "/topics/" + childId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"title":"Bulkhead Pattern","slug":"bulkhead","parentId":"%s","sortOrder":0}
                    """
                        .formatted(rootId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Bulkhead Pattern"));

    mockMvc
        .perform(
            delete(BASE + "/topics/" + childId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk());
  }

  @Test
  void shouldRejectUnauthenticatedAccessAndValidationErrors() throws Exception {
    mockMvc.perform(get(BASE + "/tree")).andExpect(status().isUnauthorized());

    String email = AuthApiTestSupport.uniqueEmail("tutorial-val");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    mockMvc
        .perform(
            post(BASE + "/topics")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"title":"","slug":null,"parentId":null,"sortOrder":null}
                    """))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            get(BASE + "/topics/by-path")
                .param("path", "missing-topic")
                .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldIsolateTopicsBetweenUsers() throws Exception {
    String emailA = AuthApiTestSupport.uniqueEmail("tutorial-a");
    String emailB = AuthApiTestSupport.uniqueEmail("tutorial-b");
    AuthApiTestSupport.register(mockMvc, emailA);
    AuthApiTestSupport.register(mockMvc, emailB);
    String tokenA = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, emailA);
    String tokenB = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, emailB);

    MvcResult createResult =
        mockMvc
            .perform(
                post(BASE + "/topics")
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {"title":"Private Topic","slug":null,"parentId":null,"sortOrder":null}
                        """))
            .andExpect(status().isCreated())
            .andReturn();
    String topicId = readId(createResult);

    mockMvc
        .perform(get(BASE + "/tree").header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(0));

    mockMvc
        .perform(
            get(BASE + "/topics/by-path")
                .param("path", "private-topic")
                .header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isNotFound());

    mockMvc
        .perform(delete(BASE + "/topics/" + topicId).header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isNotFound());
  }

  private String readId(MvcResult result) throws Exception {
    JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
    return root.path("data").path("id").asText();
  }
}
