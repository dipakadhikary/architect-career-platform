package com.acos.learning.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

/** End-to-end integration tests for the authenticated learning roadmap APIs. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LearningIntegrationTest {

  private static final String PLANS_PATH = "/api/v1/learning/plans";

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, true);
  }

  @Test
  void shouldSupportFullLearningRoadmapLifecycleAndProgress() throws Exception {
    String email = AuthApiTestSupport.uniqueEmail("learning");
    AuthApiTestSupport.register(mockMvc, email);
    String accessToken = AuthApiTestSupport.loginAccessToken(mockMvc, objectMapper, email);

    MvcResult planResult =
        mockMvc
            .perform(
                post(PLANS_PATH)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"System Design Mastery",
                          "description":"Twelve-week plan",
                          "status":"ACTIVE",
                          "targetDate":"2026-12-31"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("System Design Mastery"))
            .andExpect(jsonPath("$.data.progressPercent").value(0))
            .andReturn();

    String planId =
        objectMapper
            .readTree(planResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    MvcResult milestoneResult =
        mockMvc
            .perform(
                post(PLANS_PATH + "/" + planId + "/milestones")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"Consistency Models",
                          "description":"Core consistency concepts"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.title").value("Consistency Models"))
            .andExpect(jsonPath("$.data.sortOrder").value(0))
            .andReturn();

    String milestoneId =
        objectMapper
            .readTree(milestoneResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    MvcResult topicOneResult =
        mockMvc
            .perform(
                post(PLANS_PATH + "/" + planId + "/milestones/" + milestoneId + "/topics")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title":"CAP Theorem",
                          "description":"Trade-offs"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.status").value("NOT_STARTED"))
            .andReturn();

    String topicOneId =
        objectMapper
            .readTree(topicOneResult.getResponse().getContentAsString())
            .path("data")
            .path("id")
            .asText();

    mockMvc
        .perform(
            post(PLANS_PATH + "/" + planId + "/milestones/" + milestoneId + "/topics")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"Quorum Reads",
                      "status":"IN_PROGRESS"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

    mockMvc
        .perform(
            patch(
                    PLANS_PATH
                        + "/"
                        + planId
                        + "/milestones/"
                        + milestoneId
                        + "/topics/"
                        + topicOneId
                        + "/status")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {"status":"COMPLETED"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("COMPLETED"));

    mockMvc
        .perform(get(PLANS_PATH + "/" + planId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalTopics").value(2))
        .andExpect(jsonPath("$.data.completedTopics").value(1))
        .andExpect(jsonPath("$.data.progressPercent").value(50))
        .andExpect(jsonPath("$.data.milestones[0].progressPercent").value(50));

    mockMvc
        .perform(
            put(PLANS_PATH + "/" + planId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(APPLICATION_JSON)
                .content(
                    """
                    {
                      "title":"System Design Mastery Updated",
                      "status":"ACTIVE"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("System Design Mastery Updated"));

    mockMvc
        .perform(get(PLANS_PATH).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].progressPercent").value(50));

    mockMvc
        .perform(delete(PLANS_PATH + "/" + planId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    mockMvc
        .perform(get(PLANS_PATH + "/" + planId).header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  void shouldRejectLearningApisWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get(PLANS_PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }
}
