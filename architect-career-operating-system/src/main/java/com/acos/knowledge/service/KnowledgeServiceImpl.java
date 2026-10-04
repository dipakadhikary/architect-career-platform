package com.acos.knowledge.service;

import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteRequest;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.entity.Category;
import com.acos.knowledge.entity.KnowledgeNote;
import com.acos.knowledge.entity.Tag;
import com.acos.knowledge.event.KnowledgeCreatedEvent;
import com.acos.knowledge.event.KnowledgeDeletedEvent;
import com.acos.knowledge.event.KnowledgeDomainEventPublisher;
import com.acos.knowledge.event.KnowledgeUpdatedEvent;
import com.acos.knowledge.exception.KnowledgeNoteNotFoundException;
import com.acos.knowledge.exception.KnowledgeNoteVersionConflictException;
import com.acos.knowledge.mapper.KnowledgeNoteMapper;
import com.acos.knowledge.repository.CategoryRepository;
import com.acos.knowledge.repository.KnowledgeNoteRepository;
import com.acos.knowledge.repository.TagRepository;
import com.acos.knowledge.validator.KnowledgeNoteValidator;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link KnowledgeService} implementation. */
@Service
@Transactional
public class KnowledgeServiceImpl implements KnowledgeService {

  private final KnowledgeNoteRepository knowledgeNoteRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final KnowledgeNoteMapper knowledgeNoteMapper;
  private final KnowledgeNoteValidator knowledgeNoteValidator;
  private final KnowledgeDomainEventPublisher knowledgeDomainEventPublisher;

  /**
   * Creates the knowledge service.
   *
   * @param knowledgeNoteRepository note repository
   * @param categoryRepository category repository
   * @param tagRepository tag repository
   * @param knowledgeNoteMapper mapper
   * @param knowledgeNoteValidator validator
   * @param knowledgeDomainEventPublisher domain event publisher
   */
  public KnowledgeServiceImpl(
      KnowledgeNoteRepository knowledgeNoteRepository,
      CategoryRepository categoryRepository,
      TagRepository tagRepository,
      KnowledgeNoteMapper knowledgeNoteMapper,
      KnowledgeNoteValidator knowledgeNoteValidator,
      KnowledgeDomainEventPublisher knowledgeDomainEventPublisher) {
    this.knowledgeNoteRepository =
        Objects.requireNonNull(knowledgeNoteRepository, "knowledgeNoteRepository must not be null");
    this.categoryRepository =
        Objects.requireNonNull(categoryRepository, "categoryRepository must not be null");
    this.tagRepository = Objects.requireNonNull(tagRepository, "tagRepository must not be null");
    this.knowledgeNoteMapper =
        Objects.requireNonNull(knowledgeNoteMapper, "knowledgeNoteMapper must not be null");
    this.knowledgeNoteValidator =
        Objects.requireNonNull(knowledgeNoteValidator, "knowledgeNoteValidator must not be null");
    this.knowledgeDomainEventPublisher =
        Objects.requireNonNull(
            knowledgeDomainEventPublisher, "knowledgeDomainEventPublisher must not be null");
  }

  @Override
  public KnowledgeNoteResponse create(UUID ownerId, KnowledgeNoteRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    String title = request.title().trim();
    String summary = request.summary().trim();
    String content = request.content();
    knowledgeNoteValidator.validateContent(content);
    String categoryName = knowledgeNoteValidator.normalizeCategoryName(request.categoryName());
    Set<String> tagNames = knowledgeNoteValidator.normalizeTagNames(request.tagNames());

    KnowledgeNote note = new KnowledgeNote(ownerId, title, summary, content);
    note.setCategory(resolveCategory(ownerId, categoryName));
    note.replaceTags(resolveTags(ownerId, tagNames));

    KnowledgeNote saved = knowledgeNoteRepository.save(note);
    knowledgeDomainEventPublisher.publish(
        new KnowledgeCreatedEvent(
            saved.getId(),
            saved.getOwnerId(),
            saved.getTitle(),
            saved.getContent(),
            tagNames(saved),
            Instant.now(),
            saved.getVersion()));
    return knowledgeNoteMapper.toResponse(saved);
  }

  @Override
  public KnowledgeNoteResponse update(UUID ownerId, UUID noteId, KnowledgeNoteRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(noteId, "noteId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    KnowledgeNote note = requireOwnedNote(ownerId, noteId);
    if (request.expectedVersion() != null && request.expectedVersion() != note.getVersion()) {
      throw new KnowledgeNoteVersionConflictException(noteId);
    }
    knowledgeNoteValidator.validateContent(request.content());

    note.setTitle(request.title().trim());
    note.setSummary(request.summary().trim());
    note.setContent(request.content());
    note.setCategory(
        resolveCategory(
            ownerId, knowledgeNoteValidator.normalizeCategoryName(request.categoryName())));

    if (request.tagNames() != null) {
      Set<String> tagNames = knowledgeNoteValidator.normalizeTagNames(request.tagNames());
      note.replaceTags(resolveTags(ownerId, tagNames));
    }

    knowledgeDomainEventPublisher.publish(
        new KnowledgeUpdatedEvent(
            note.getId(),
            note.getOwnerId(),
            note.getTitle(),
            note.getContent(),
            tagNames(note),
            Instant.now(),
            note.getVersion()));
    return knowledgeNoteMapper.toResponse(note);
  }

  @Override
  public void delete(UUID ownerId, UUID noteId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(noteId, "noteId must not be null");
    KnowledgeNote note = requireOwnedNote(ownerId, noteId);
    knowledgeDomainEventPublisher.publish(
        new KnowledgeDeletedEvent(noteId, ownerId, note.getVersion()));
    knowledgeNoteRepository.delete(note);
  }

  @Override
  @Transactional(readOnly = true)
  public KnowledgeNoteResponse get(UUID ownerId, UUID noteId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(noteId, "noteId must not be null");
    return knowledgeNoteMapper.toResponse(requireOwnedNote(ownerId, noteId));
  }

  @Override
  @Transactional(readOnly = true)
  public KnowledgeNotePageResponse list(UUID ownerId, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    knowledgeNoteValidator.validatePageable(pageable);
    Page<KnowledgeNote> page = knowledgeNoteRepository.findByOwnerId(ownerId, pageable);
    return knowledgeNoteMapper.toPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public KnowledgeNotePageResponse search(UUID ownerId, String query, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    knowledgeNoteValidator.validatePageable(pageable);
    String normalizedQuery = knowledgeNoteValidator.normalizeSearchQuery(query);
    Page<KnowledgeNote> page =
        knowledgeNoteRepository.searchByOwnerIdAndTitleOrSummary(
            ownerId, normalizedQuery, pageable);
    return knowledgeNoteMapper.toPageResponse(page);
  }

  private KnowledgeNote requireOwnedNote(UUID ownerId, UUID noteId) {
    return knowledgeNoteRepository
        .findByIdAndOwnerId(noteId, ownerId)
        .orElseThrow(() -> new KnowledgeNoteNotFoundException(noteId));
  }

  private Category resolveCategory(UUID ownerId, String categoryName) {
    if (categoryName == null) {
      return null;
    }
    return categoryRepository
        .findByOwnerIdAndName(ownerId, categoryName)
        .orElseGet(() -> categoryRepository.save(new Category(ownerId, categoryName, null)));
  }

  private Set<Tag> resolveTags(UUID ownerId, Set<String> tagNames) {
    if (tagNames.isEmpty()) {
      return Set.of();
    }
    List<Tag> existing = tagRepository.findByOwnerIdAndNameIn(ownerId, tagNames);
    Map<String, Tag> byName =
        existing.stream().collect(Collectors.toMap(Tag::getName, Function.identity()));

    List<Tag> created =
        tagNames.stream()
            .filter(tagName -> !byName.containsKey(tagName))
            .map(tagName -> new Tag(ownerId, tagName))
            .map(tagRepository::save)
            .toList();
    created.forEach(tag -> byName.put(tag.getName(), tag));

    Set<Tag> resolved = new HashSet<>();
    for (String tagName : tagNames) {
      resolved.add(byName.get(tagName));
    }
    return resolved;
  }

  private static List<String> tagNames(KnowledgeNote note) {
    return note.getTags().stream().map(Tag::getName).sorted().toList();
  }
}
