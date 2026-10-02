package com.acos.knowledge.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.knowledge.entity.Category;
import com.acos.knowledge.entity.KnowledgeNote;
import com.acos.knowledge.entity.Tag;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/** Repository slice tests for knowledge persistence. */
class KnowledgeNoteRepositoryTest extends KnowledgeRepositoryTestSupport {

  @Autowired private KnowledgeNoteRepository knowledgeNoteRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private TagRepository tagRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void shouldPersistNoteWithCategoryAndTagsAndSearchByTitleOrSummary() {
    User owner = persistOwner("knowledge-owner@acos.local");
    Category category =
        categoryRepository.saveAndFlush(new Category(owner.getId(), "Architecture", null));
    Tag tag = tagRepository.saveAndFlush(new Tag(owner.getId(), "interview"));

    KnowledgeNote note =
        new KnowledgeNote(
            owner.getId(),
            "CAP Theorem Notes",
            "Distributed systems trade-offs",
            "## Consistency\n\nAvailability vs consistency.");
    note.setCategory(category);
    note.replaceTags(Set.of(tag));
    KnowledgeNote saved = knowledgeNoteRepository.saveAndFlush(note);

    KnowledgeNote found =
        knowledgeNoteRepository.findByIdAndOwnerId(saved.getId(), owner.getId()).orElseThrow();

    assertThat(found.getTitle()).isEqualTo("CAP Theorem Notes");
    assertThat(found.getCategory().getName()).isEqualTo("Architecture");
    assertThat(found.getTags()).extracting(Tag::getName).containsExactly("interview");

    Page<KnowledgeNote> byTitle =
        knowledgeNoteRepository.searchByOwnerIdAndTitleOrSummary(
            owner.getId(), "cap theorem", PageRequest.of(0, 10));
    Page<KnowledgeNote> bySummary =
        knowledgeNoteRepository.searchByOwnerIdAndTitleOrSummary(
            owner.getId(), "trade-offs", PageRequest.of(0, 10));
    Page<KnowledgeNote> listed =
        knowledgeNoteRepository.findByOwnerId(owner.getId(), PageRequest.of(0, 10));

    assertThat(byTitle.getContent())
        .extracting(KnowledgeNote::getId)
        .containsExactly(saved.getId());
    assertThat(bySummary.getContent())
        .extracting(KnowledgeNote::getId)
        .containsExactly(saved.getId());
    assertThat(listed.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindNoteForDifferentOwner() {
    User owner = persistOwner("owner-a@acos.local");
    User other = persistOwner("owner-b@acos.local");
    KnowledgeNote saved =
        knowledgeNoteRepository.saveAndFlush(
            new KnowledgeNote(owner.getId(), "Private", "Summary", "content"));

    assertThat(knowledgeNoteRepository.findByIdAndOwnerId(saved.getId(), other.getId())).isEmpty();
  }

  private User persistOwner(String email) {
    return userRepository.saveAndFlush(
        new User(email, "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV", "Ada", "Lovelace"));
  }
}
