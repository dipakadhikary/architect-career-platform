package com.acos.tutorial.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.common.exception.ValidationException;
import com.acos.tutorial.dto.TutorialConceptRequest;
import com.acos.tutorial.dto.TutorialQuestionRequest;
import com.acos.tutorial.dto.TutorialTopicRequest;
import com.acos.tutorial.entity.TutorialConcept;
import com.acos.tutorial.entity.TutorialQuestion;
import com.acos.tutorial.entity.TutorialTopic;
import com.acos.tutorial.event.TutorialIndexPublisher;
import com.acos.tutorial.exception.TutorialCircularHierarchyException;
import com.acos.tutorial.exception.TutorialDuplicatePathException;
import com.acos.tutorial.exception.TutorialQuestionNotFoundException;
import com.acos.tutorial.exception.TutorialTopicNotFoundException;
import com.acos.tutorial.repository.TutorialConceptRepository;
import com.acos.tutorial.repository.TutorialQuestionRepository;
import com.acos.tutorial.repository.TutorialSearchRepository;
import com.acos.tutorial.repository.TutorialTopicRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TutorialServiceImplTest {

  @Mock private TutorialTopicRepository topicRepository;
  @Mock private TutorialConceptRepository conceptRepository;
  @Mock private TutorialQuestionRepository questionRepository;
  @Mock private TutorialSearchRepository searchRepository;
  @Mock private TutorialIndexPublisher tutorialIndexPublisher;

  private TutorialServiceImpl service;
  private UUID ownerId;

  @BeforeEach
  void setUp() {
    service =
        new TutorialServiceImpl(
            topicRepository,
            conceptRepository,
            questionRepository,
            searchRepository,
            tutorialIndexPublisher);
    ownerId = UUID.randomUUID();
  }

  @Test
  void shouldCreateRootTopic() {
    when(topicRepository.findMaxSortOrder(eq(ownerId), isNull())).thenReturn(-1);
    when(topicRepository.existsPath(eq(ownerId), eq("design-patterns"), isNull()))
        .thenReturn(false);
    when(topicRepository.save(any(TutorialTopic.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(conceptRepository.existsByTopicId(any())).thenReturn(false);
    when(questionRepository.existsByTopicId(any())).thenReturn(false);
    when(topicRepository.findByOwnerIdAndParentIdOrderBySortOrderAsc(eq(ownerId), any()))
        .thenReturn(List.of());

    var response =
        service.createTopic(ownerId, new TutorialTopicRequest("Design Patterns", null, null, null));

    assertThat(response.title()).isEqualTo("Design Patterns");
    assertThat(response.slug()).isEqualTo("design-patterns");
    assertThat(response.path()).isEqualTo("design-patterns");
    assertThat(response.breadcrumb()).hasSize(1);
    ArgumentCaptor<TutorialTopic> captor = ArgumentCaptor.forClass(TutorialTopic.class);
    verify(topicRepository).save(captor.capture());
    assertThat(captor.getValue().getSortOrder()).isZero();
  }

  @Test
  void shouldCreateChildAndSiblingTopics() {
    UUID parentId = UUID.randomUUID();
    TutorialTopic parent = new TutorialTopic(ownerId, null, "Java", "java", "java", 0);
    setId(parent, parentId);

    when(topicRepository.findByIdAndOwnerId(parentId, ownerId)).thenReturn(Optional.of(parent));
    when(topicRepository.findMaxSortOrder(ownerId, parentId)).thenReturn(0);
    when(topicRepository.existsPath(eq(ownerId), eq("java/oops"), isNull())).thenReturn(false);
    when(topicRepository.save(any(TutorialTopic.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(conceptRepository.existsByTopicId(any())).thenReturn(false);
    when(questionRepository.existsByTopicId(any())).thenReturn(false);
    when(topicRepository.findByOwnerIdAndParentIdOrderBySortOrderAsc(eq(ownerId), any()))
        .thenReturn(List.of());

    var child =
        service.createTopic(ownerId, new TutorialTopicRequest("OOPS", null, parentId, null));
    assertThat(child.path()).isEqualTo("java/oops");
    assertThat(child.parentId()).isEqualTo(parentId);

    when(topicRepository.existsPath(eq(ownerId), eq("java/collections"), isNull()))
        .thenReturn(false);
    when(topicRepository.findMaxSortOrder(ownerId, parentId)).thenReturn(1);
    var sibling =
        service.createTopic(ownerId, new TutorialTopicRequest("Collections", null, parentId, null));
    assertThat(sibling.path()).isEqualTo("java/collections");
  }

  @Test
  void shouldBuildDeepHierarchyTree() {
    TutorialTopic root = new TutorialTopic(ownerId, null, "A", "a", "a", 0);
    TutorialTopic b = new TutorialTopic(ownerId, root, "B", "b", "a/b", 0);
    TutorialTopic c = new TutorialTopic(ownerId, b, "C", "c", "a/b/c", 0);
    TutorialTopic d = new TutorialTopic(ownerId, c, "D", "d", "a/b/c/d", 0);
    setId(root, UUID.randomUUID());
    setId(b, UUID.randomUUID());
    setId(c, UUID.randomUUID());
    setId(d, UUID.randomUUID());

    when(topicRepository.findByOwnerIdOrderByPathAsc(ownerId)).thenReturn(List.of(root, b, c, d));
    when(conceptRepository.existsByTopicId(any())).thenReturn(false);
    when(questionRepository.existsByTopicId(any())).thenReturn(false);

    var tree = service.getTree(ownerId);
    assertThat(tree).hasSize(1);
    assertThat(tree.getFirst().children().getFirst().children().getFirst().children())
        .extracting(node -> node.title())
        .containsExactly("D");
  }

  @Test
  void shouldRejectBlankTitle() {
    assertThatThrownBy(
            () -> service.createTopic(ownerId, new TutorialTopicRequest("  ", null, null, null)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldRejectDuplicatePath() {
    when(topicRepository.existsPath(eq(ownerId), eq("java"), isNull())).thenReturn(true);
    assertThatThrownBy(
            () -> service.createTopic(ownerId, new TutorialTopicRequest("Java", null, null, null)))
        .isInstanceOf(TutorialDuplicatePathException.class);
  }

  @Test
  void shouldPreventCircularHierarchy() {
    UUID parentId = UUID.randomUUID();
    UUID childId = UUID.randomUUID();
    TutorialTopic parent = new TutorialTopic(ownerId, null, "A", "a", "a", 0);
    TutorialTopic child = new TutorialTopic(ownerId, parent, "B", "b", "a/b", 0);
    setId(parent, parentId);
    setId(child, childId);

    when(topicRepository.findByIdAndOwnerId(childId, ownerId)).thenReturn(Optional.of(child));
    when(topicRepository.findByIdAndOwnerId(parentId, ownerId)).thenReturn(Optional.of(parent));

    assertThatThrownBy(
            () ->
                service.updateTopic(
                    ownerId, parentId, new TutorialTopicRequest("A", "a", childId, 0)))
        .isInstanceOf(TutorialCircularHierarchyException.class);
  }

  @Test
  void shouldThrowWhenTopicMissing() {
    UUID topicId = UUID.randomUUID();
    when(topicRepository.findByIdAndOwnerId(topicId, ownerId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.deleteTopic(ownerId, topicId))
        .isInstanceOf(TutorialTopicNotFoundException.class);
  }

  @Test
  void shouldCreateAndUpdateConcept() {
    UUID topicId = UUID.randomUUID();
    TutorialTopic topic = new TutorialTopic(ownerId, null, "Factory", "factory", "factory", 0);
    setId(topic, topicId);
    when(topicRepository.findByIdAndOwnerId(topicId, ownerId)).thenReturn(Optional.of(topic));
    when(conceptRepository.findByTopicId(topicId)).thenReturn(Optional.empty());
    when(conceptRepository.save(any(TutorialConcept.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var created =
        service.upsertConcept(ownerId, topicId, new TutorialConceptRequest("# Factory Pattern"));
    assertThat(created.content()).contains("Factory");

    TutorialConcept existing = new TutorialConcept(topic, "old");
    when(conceptRepository.findByTopicId(topicId)).thenReturn(Optional.of(existing));
    var updated =
        service.upsertConcept(ownerId, topicId, new TutorialConceptRequest("updated concept"));
    assertThat(updated.content()).isEqualTo("updated concept");
  }

  @Test
  void shouldCreateUpdateAndListMultipleQuestions() {
    UUID topicId = UUID.randomUUID();
    TutorialTopic topic = new TutorialTopic(ownerId, null, "QA", "qa", "qa", 0);
    setId(topic, topicId);
    when(topicRepository.findByIdAndOwnerId(topicId, ownerId)).thenReturn(Optional.of(topic));
    when(questionRepository.findMaxSortOrder(topicId)).thenReturn(-1);
    when(questionRepository.save(any(TutorialQuestion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var q1 =
        service.createQuestion(ownerId, topicId, new TutorialQuestionRequest("Q1?", "A1", null));
    assertThat(q1.sortOrder()).isZero();

    when(questionRepository.findMaxSortOrder(topicId)).thenReturn(0);
    var q2 =
        service.createQuestion(ownerId, topicId, new TutorialQuestionRequest("Q2?", "A2", null));
    assertThat(q2.sortOrder()).isEqualTo(1);

    UUID questionId = UUID.randomUUID();
    TutorialQuestion entity = new TutorialQuestion(topic, "Q1?", "A1", 0);
    setQuestionId(entity, questionId);
    when(questionRepository.findByIdAndTopic_OwnerId(questionId, ownerId))
        .thenReturn(Optional.of(entity));
    var updated =
        service.updateQuestion(
            ownerId, questionId, new TutorialQuestionRequest("Q1 updated?", "A1 updated", 0));
    assertThat(updated.question()).contains("updated");

    when(topicRepository.findByOwnerIdAndPath(ownerId, "qa")).thenReturn(Optional.of(topic));
    when(questionRepository.findByTopicIdOrderBySortOrderAsc(topicId)).thenReturn(List.of(entity));
    var page = service.getQuestionsByPath(ownerId, "qa");
    assertThat(page.questions()).hasSize(1);
  }

  @Test
  void shouldMapSearchHitsAndSanitizeSnippets() {
    UUID topicId = UUID.randomUUID();
    TutorialTopic topic = new TutorialTopic(ownerId, null, "Bulkhead", "bulkhead", "bulkhead", 0);
    setId(topic, topicId);
    when(searchRepository.count(ownerId, "bulkhead")).thenReturn(1L);
    when(searchRepository.search(ownerId, "bulkhead", 20, 0))
        .thenReturn(
            List.of(
                new TutorialSearchRepository.SearchHit(
                    topicId,
                    "Bulkhead",
                    "bulkhead",
                    "CONCEPT",
                    "<b>unsafe</b> bulkhead <mark>isolates</mark>",
                    0.8)));
    when(topicRepository.findByIdAndOwnerId(topicId, ownerId)).thenReturn(Optional.of(topic));

    var page = service.search(ownerId, "bulkhead", PageRequest.of(0, 20));
    assertThat(page.content()).hasSize(1);
    assertThat(page.content().getFirst().contentType()).isEqualTo("Concept");
    assertThat(page.content().getFirst().snippet()).doesNotContain("<b>");
    assertThat(page.content().getFirst().snippet()).contains("<mark>isolates</mark>");
    assertThat(page.content().getFirst().rank()).isEqualTo(0.8);
  }

  @Test
  void shouldRejectBlankSearchQuery() {
    assertThatThrownBy(() -> service.search(ownerId, "  ", PageRequest.of(0, 20)))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void shouldThrowWhenQuestionMissing() {
    UUID questionId = UUID.randomUUID();
    when(questionRepository.findByIdAndTopic_OwnerId(questionId, ownerId))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.deleteQuestion(ownerId, questionId))
        .isInstanceOf(TutorialQuestionNotFoundException.class);
  }

  private static void setId(TutorialTopic topic, UUID id) {
    try {
      var field = topic.getClass().getSuperclass().getDeclaredField("id");
      field.setAccessible(true);
      field.set(topic, id);
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static void setQuestionId(TutorialQuestion question, UUID id) {
    try {
      var field = question.getClass().getSuperclass().getDeclaredField("id");
      field.setAccessible(true);
      field.set(question, id);
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
