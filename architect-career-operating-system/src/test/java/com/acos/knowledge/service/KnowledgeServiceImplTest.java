package com.acos.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteRequest;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.entity.Category;
import com.acos.knowledge.entity.KnowledgeNote;
import com.acos.knowledge.entity.Tag;
import com.acos.knowledge.event.KnowledgeDeletedEvent;
import com.acos.knowledge.event.KnowledgeDomainEventPublisher;
import com.acos.knowledge.exception.KnowledgeNoteNotFoundException;
import com.acos.knowledge.mapper.KnowledgeNoteMapper;
import com.acos.knowledge.repository.CategoryRepository;
import com.acos.knowledge.repository.KnowledgeNoteRepository;
import com.acos.knowledge.repository.TagRepository;
import com.acos.knowledge.validator.KnowledgeNoteValidator;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link KnowledgeServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class KnowledgeServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID NOTE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private KnowledgeNoteRepository knowledgeNoteRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private TagRepository tagRepository;
  @Mock private KnowledgeNoteMapper knowledgeNoteMapper;
  @Mock private KnowledgeNoteValidator knowledgeNoteValidator;
  @Mock private KnowledgeDomainEventPublisher knowledgeDomainEventPublisher;

  private KnowledgeServiceImpl knowledgeService;

  @BeforeEach
  void setUp() {
    knowledgeService =
        new KnowledgeServiceImpl(
            knowledgeNoteRepository,
            categoryRepository,
            tagRepository,
            knowledgeNoteMapper,
            knowledgeNoteValidator,
            knowledgeDomainEventPublisher);
  }

  @Test
  void shouldCreateNoteWithCategoryAndTags() {
    KnowledgeNoteRequest request =
        new KnowledgeNoteRequest(
            " Title ", " Summary ", "# Markdown", "System Design", List.of("Interview", "CAP"));
    Category category = new Category(OWNER_ID, "System Design", null);
    Tag interview = new Tag(OWNER_ID, "interview");
    Tag cap = new Tag(OWNER_ID, "cap");
    KnowledgeNoteResponse expected =
        new KnowledgeNoteResponse(
            NOTE_ID,
            "Title",
            "Summary",
            "# Markdown",
            null,
            List.of("cap", "interview"),
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));

    when(knowledgeNoteValidator.normalizeCategoryName("System Design")).thenReturn("System Design");
    when(knowledgeNoteValidator.normalizeTagNames(List.of("Interview", "CAP")))
        .thenReturn(Set.of("interview", "cap"));
    when(categoryRepository.findByOwnerIdAndName(OWNER_ID, "System Design"))
        .thenReturn(Optional.of(category));
    when(tagRepository.findByOwnerIdAndNameIn(eq(OWNER_ID), any()))
        .thenReturn(List.of(interview, cap));
    when(knowledgeNoteRepository.save(any(KnowledgeNote.class)))
        .thenAnswer(
            invocation -> {
              KnowledgeNote note = invocation.getArgument(0);
              ReflectionTestUtils.setField(note, "id", NOTE_ID);
              return note;
            });
    when(knowledgeNoteMapper.toResponse(any(KnowledgeNote.class))).thenReturn(expected);

    KnowledgeNoteResponse response = knowledgeService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    verify(knowledgeNoteValidator).validateContent("# Markdown");
    ArgumentCaptor<KnowledgeNote> noteCaptor = ArgumentCaptor.forClass(KnowledgeNote.class);
    verify(knowledgeNoteRepository).save(noteCaptor.capture());
    assertThat(noteCaptor.getValue().getTitle()).isEqualTo("Title");
    assertThat(noteCaptor.getValue().getSummary()).isEqualTo("Summary");
    assertThat(noteCaptor.getValue().getCategory()).isEqualTo(category);
    assertThat(noteCaptor.getValue().getTags()).containsExactlyInAnyOrder(interview, cap);
  }

  @Test
  void shouldUpdateOwnedNote() {
    KnowledgeNote existing = new KnowledgeNote(OWNER_ID, "Old", "Old summary", "old content");
    ReflectionTestUtils.setField(existing, "id", NOTE_ID);
    KnowledgeNoteRequest request =
        new KnowledgeNoteRequest("New", "New summary", "new content", null, List.of());
    KnowledgeNoteResponse expected =
        new KnowledgeNoteResponse(
            NOTE_ID,
            "New",
            "New summary",
            "new content",
            null,
            List.of(),
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T07:00:00Z"));

    when(knowledgeNoteRepository.findByIdAndOwnerId(NOTE_ID, OWNER_ID))
        .thenReturn(Optional.of(existing));
    when(knowledgeNoteValidator.normalizeCategoryName(null)).thenReturn(null);
    when(knowledgeNoteValidator.normalizeTagNames(List.of())).thenReturn(Set.of());
    when(knowledgeNoteMapper.toResponse(existing)).thenReturn(expected);

    KnowledgeNoteResponse response = knowledgeService.update(OWNER_ID, NOTE_ID, request);

    assertThat(response).isEqualTo(expected);
    assertThat(existing.getTitle()).isEqualTo("New");
    assertThat(existing.getSummary()).isEqualTo("New summary");
    assertThat(existing.getContent()).isEqualTo("new content");
    assertThat(existing.getCategory()).isNull();
    assertThat(existing.getTags()).isEmpty();
  }

  @Test
  void shouldDeleteOwnedNote() {
    KnowledgeNote existing = new KnowledgeNote(OWNER_ID, "Title", "Summary", "content");
    when(knowledgeNoteRepository.findByIdAndOwnerId(NOTE_ID, OWNER_ID))
        .thenReturn(Optional.of(existing));

    knowledgeService.delete(OWNER_ID, NOTE_ID);

    verify(knowledgeNoteRepository).delete(existing);
    verify(knowledgeDomainEventPublisher).publish(any(KnowledgeDeletedEvent.class));
  }

  @Test
  void shouldFailWhenNoteMissing() {
    when(knowledgeNoteRepository.findByIdAndOwnerId(NOTE_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> knowledgeService.get(OWNER_ID, NOTE_ID))
        .isInstanceOf(KnowledgeNoteNotFoundException.class);
  }

  @Test
  void shouldListNotes() {
    Pageable pageable = PageRequest.of(0, 20);
    KnowledgeNote note = new KnowledgeNote(OWNER_ID, "Title", "Summary", "content");
    Page<KnowledgeNote> page = new PageImpl<>(List.of(note), pageable, 1);
    KnowledgeNotePageResponse expected =
        new KnowledgeNotePageResponse(List.of(), 0, 20, 1, 1, true, true);

    when(knowledgeNoteRepository.findByOwnerId(OWNER_ID, pageable)).thenReturn(page);
    when(knowledgeNoteMapper.toPageResponse(page)).thenReturn(expected);

    KnowledgeNotePageResponse response = knowledgeService.list(OWNER_ID, pageable);

    assertThat(response).isEqualTo(expected);
    verify(knowledgeNoteValidator).validatePageable(pageable);
  }

  @Test
  void shouldSearchNotes() {
    Pageable pageable = PageRequest.of(0, 20);
    KnowledgeNote note = new KnowledgeNote(OWNER_ID, "CAP notes", "summary", "content");
    Page<KnowledgeNote> page = new PageImpl<>(List.of(note), pageable, 1);
    KnowledgeNotePageResponse expected =
        new KnowledgeNotePageResponse(List.of(), 0, 20, 1, 1, true, true);

    when(knowledgeNoteValidator.normalizeSearchQuery("cap")).thenReturn("cap");
    when(knowledgeNoteRepository.searchByOwnerIdAndTitleOrSummary(OWNER_ID, "cap", pageable))
        .thenReturn(page);
    when(knowledgeNoteMapper.toPageResponse(page)).thenReturn(expected);

    KnowledgeNotePageResponse response = knowledgeService.search(OWNER_ID, "cap", pageable);

    assertThat(response).isEqualTo(expected);
    verify(tagRepository, never()).save(any());
  }
}
