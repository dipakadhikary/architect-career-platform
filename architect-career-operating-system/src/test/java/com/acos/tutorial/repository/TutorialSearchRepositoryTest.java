package com.acos.tutorial.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.config.JpaAuditingConfiguration;
import com.acos.testsupport.PostgresTestSupport;
import com.acos.tutorial.entity.TutorialConcept;
import com.acos.tutorial.entity.TutorialQuestion;
import com.acos.tutorial.entity.TutorialTopic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, TutorialSearchRepositoryImpl.class})
class TutorialSearchRepositoryTest {

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @Autowired private TutorialTopicRepository topicRepository;
  @Autowired private TutorialConceptRepository conceptRepository;
  @Autowired private TutorialQuestionRepository questionRepository;
  @Autowired private TutorialSearchRepository searchRepository;
  @Autowired private UserRepository userRepository;

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, false);
  }

  @Test
  void shouldPersistHierarchyAndSearchAcrossConceptQuestionsAndAnswers() {
    User owner =
        userRepository.saveAndFlush(
            new User(
                "tutorial-fts-" + UUID.randomUUID() + "@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Tutorial",
                "Owner"));
    UUID ownerId = owner.getId();

    TutorialTopic root =
        topicRepository.saveAndFlush(
            new TutorialTopic(ownerId, null, "Patterns", "patterns", "patterns", 0));
    TutorialTopic child =
        topicRepository.saveAndFlush(
            new TutorialTopic(ownerId, root, "Bulkhead", "bulkhead", "patterns/bulkhead", 0));

    conceptRepository.saveAndFlush(
        new TutorialConcept(
            child, "The bulkhead pattern isolates failures between different components."));
    questionRepository.saveAndFlush(
        new TutorialQuestion(
            child,
            "What happens when a circuit breaker opens?",
            "Calls fail fast until recovery.",
            0));

    List<TutorialTopic> descendants =
        topicRepository.findDescendantsByPathPrefix(ownerId, "patterns");
    assertThat(descendants).extracting(TutorialTopic::getPath).containsExactly("patterns/bulkhead");

    assertThat(topicRepository.findByOwnerIdAndPath(ownerId, "patterns/bulkhead")).isPresent();

    List<TutorialSearchRepository.SearchHit> conceptHits =
        searchRepository.search(ownerId, "bulkhead isolates", 10, 0);
    assertThat(conceptHits).isNotEmpty();
    assertThat(conceptHits.getFirst().contentType()).isEqualTo("CONCEPT");
    assertThat(conceptHits.getFirst().snippet()).isNotBlank();
    assertThat(conceptHits.getFirst().rank()).isGreaterThan(0);

    List<TutorialSearchRepository.SearchHit> questionHits =
        searchRepository.search(ownerId, "circuit breaker", 10, 0);
    assertThat(questionHits).isNotEmpty();
    assertThat(questionHits.getFirst().contentType()).isEqualTo("QUESTIONS_AND_ANSWERS");

    List<TutorialSearchRepository.SearchHit> answerHits =
        searchRepository.search(ownerId, "fail fast", 10, 0);
    assertThat(answerHits).isNotEmpty();

    assertThat(searchRepository.count(ownerId, "zzznomatchxyz")).isZero();
    assertThat(searchRepository.search(ownerId, "zzznomatchxyz", 10, 0)).isEmpty();
  }
}
