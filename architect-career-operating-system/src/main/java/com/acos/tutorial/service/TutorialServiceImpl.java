package com.acos.tutorial.service;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import com.acos.tutorial.dto.TutorialBreadcrumbItem;
import com.acos.tutorial.dto.TutorialConceptRequest;
import com.acos.tutorial.dto.TutorialConceptResponse;
import com.acos.tutorial.dto.TutorialQuestionRequest;
import com.acos.tutorial.dto.TutorialQuestionResponse;
import com.acos.tutorial.dto.TutorialQuestionsPageResponse;
import com.acos.tutorial.dto.TutorialSearchPageResponse;
import com.acos.tutorial.dto.TutorialSearchResultResponse;
import com.acos.tutorial.dto.TutorialTopicRequest;
import com.acos.tutorial.dto.TutorialTopicResponse;
import com.acos.tutorial.dto.TutorialTreeNodeResponse;
import com.acos.tutorial.entity.TutorialConcept;
import com.acos.tutorial.entity.TutorialQuestion;
import com.acos.tutorial.entity.TutorialTopic;
import com.acos.tutorial.exception.TutorialCircularHierarchyException;
import com.acos.tutorial.exception.TutorialDuplicatePathException;
import com.acos.tutorial.exception.TutorialQuestionNotFoundException;
import com.acos.tutorial.exception.TutorialTopicNotFoundException;
import com.acos.tutorial.repository.TutorialConceptRepository;
import com.acos.tutorial.repository.TutorialQuestionRepository;
import com.acos.tutorial.repository.TutorialSearchRepository;
import com.acos.tutorial.repository.TutorialTopicRepository;
import com.acos.tutorial.util.TutorialSlugger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link TutorialService} implementation. */
@Service
@Transactional
public class TutorialServiceImpl implements TutorialService {

  private static final int MAX_CONTENT_LENGTH = 100_000;
  private static final int MAX_QA_LENGTH = 50_000;
  private static final int MAX_PAGE_SIZE = 50;

  private final TutorialTopicRepository topicRepository;
  private final TutorialConceptRepository conceptRepository;
  private final TutorialQuestionRepository questionRepository;
  private final TutorialSearchRepository searchRepository;

  public TutorialServiceImpl(
      TutorialTopicRepository topicRepository,
      TutorialConceptRepository conceptRepository,
      TutorialQuestionRepository questionRepository,
      TutorialSearchRepository searchRepository) {
    this.topicRepository = topicRepository;
    this.conceptRepository = conceptRepository;
    this.questionRepository = questionRepository;
    this.searchRepository = searchRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<TutorialTreeNodeResponse> getTree(UUID ownerId) {
    List<TutorialTopic> topics = topicRepository.findByOwnerIdOrderByPathAsc(ownerId);
    Map<UUID, List<TutorialTopic>> childrenByParent = new HashMap<>();
    List<TutorialTopic> roots = new ArrayList<>();
    for (TutorialTopic topic : topics) {
      if (topic.getParent() == null) {
        roots.add(topic);
      } else {
        childrenByParent
            .computeIfAbsent(topic.getParent().getId(), ignored -> new ArrayList<>())
            .add(topic);
      }
    }
    roots.sort(Comparator.comparingInt(TutorialTopic::getSortOrder));
    childrenByParent
        .values()
        .forEach(list -> list.sort(Comparator.comparingInt(TutorialTopic::getSortOrder)));
    return roots.stream().map(root -> toTreeNode(root, childrenByParent)).toList();
  }

  @Override
  public TutorialTopicResponse createTopic(UUID ownerId, TutorialTopicRequest request) {
    validateTitle(request.title());
    TutorialTopic parent = resolveParent(ownerId, request.parentId());
    String slug = resolveSlug(request.title(), request.slug());
    String path = buildPath(parent, slug);
    ensureUniquePath(ownerId, path, null);
    int sortOrder = resolveSortOrder(ownerId, parent, request.sortOrder());
    TutorialTopic topic =
        new TutorialTopic(ownerId, parent, request.title().trim(), slug, path, sortOrder);
    return toTopicResponse(topicRepository.save(topic));
  }

  @Override
  public TutorialTopicResponse updateTopic(
      UUID ownerId, UUID topicId, TutorialTopicRequest request) {
    TutorialTopic topic = requireTopic(ownerId, topicId);
    validateTitle(request.title());

    TutorialTopic newParent = resolveParent(ownerId, request.parentId());
    if (newParent != null && isAncestorOrSelf(topic, newParent)) {
      throw new TutorialCircularHierarchyException();
    }

    String slug =
        resolveSlug(request.title(), request.slug() == null ? topic.getSlug() : request.slug());
    String oldPath = topic.getPath();
    String newPath = buildPath(newParent, slug);
    ensureUniquePath(ownerId, newPath, topic.getId());

    topic.setTitle(request.title().trim());
    topic.setSlug(slug);
    topic.setParent(newParent);
    topic.setPath(newPath);
    if (request.sortOrder() != null) {
      if (request.sortOrder() < 0) {
        throw validation("sortOrder", "must not be negative");
      }
      topic.setSortOrder(request.sortOrder());
    }

    if (!Objects.equals(oldPath, newPath)) {
      repathDescendants(ownerId, oldPath, newPath);
    }
    return toTopicResponse(topicRepository.save(topic));
  }

  @Override
  public void deleteTopic(UUID ownerId, UUID topicId) {
    TutorialTopic topic = requireTopic(ownerId, topicId);
    topicRepository.delete(topic);
  }

  @Override
  @Transactional(readOnly = true)
  public TutorialTopicResponse getTopicByPath(UUID ownerId, String path) {
    return toTopicResponse(requireTopicByPath(ownerId, normalizePath(path)));
  }

  @Override
  @Transactional(readOnly = true)
  public TutorialConceptResponse getConceptByPath(UUID ownerId, String path) {
    TutorialTopic topic = requireTopicByPath(ownerId, normalizePath(path));
    TutorialConcept concept =
        conceptRepository
            .findByTopicId(topic.getId())
            .orElseThrow(
                () ->
                    new TutorialTopicNotFoundException(
                        "Concept for path '" + topic.getPath() + "'"));
    return new TutorialConceptResponse(
        topic.getId(),
        topic.getTitle(),
        topic.getPath(),
        buildBreadcrumb(topic),
        concept.getContent(),
        concept.getUpdatedAt());
  }

  @Override
  public TutorialConceptResponse upsertConcept(
      UUID ownerId, UUID topicId, TutorialConceptRequest request) {
    TutorialTopic topic = requireTopic(ownerId, topicId);
    String content = requireContent(request.content(), MAX_CONTENT_LENGTH, "content");
    TutorialConcept concept =
        conceptRepository
            .findByTopicId(topicId)
            .orElseGet(() -> new TutorialConcept(topic, content));
    concept.setContent(content);
    TutorialConcept saved = conceptRepository.save(concept);
    return new TutorialConceptResponse(
        topic.getId(),
        topic.getTitle(),
        topic.getPath(),
        buildBreadcrumb(topic),
        saved.getContent(),
        saved.getUpdatedAt());
  }

  @Override
  @Transactional(readOnly = true)
  public TutorialQuestionsPageResponse getQuestionsByPath(UUID ownerId, String path) {
    TutorialTopic topic = requireTopicByPath(ownerId, normalizePath(path));
    List<TutorialQuestionResponse> questions =
        questionRepository.findByTopicIdOrderBySortOrderAsc(topic.getId()).stream()
            .map(this::toQuestionResponse)
            .toList();
    return new TutorialQuestionsPageResponse(
        topic.getTitle(), topic.getPath(), buildBreadcrumb(topic), questions);
  }

  @Override
  public TutorialQuestionResponse createQuestion(
      UUID ownerId, UUID topicId, TutorialQuestionRequest request) {
    TutorialTopic topic = requireTopic(ownerId, topicId);
    String question = requireContent(request.question(), MAX_QA_LENGTH, "question");
    String answer = requireContent(request.answer(), MAX_QA_LENGTH, "answer");
    int sortOrder;
    if (request.sortOrder() == null) {
      sortOrder = questionRepository.findMaxSortOrder(topicId) + 1;
    } else if (request.sortOrder() < 0) {
      throw validation("sortOrder", "must not be negative");
    } else {
      sortOrder = request.sortOrder();
    }
    TutorialQuestion entity = new TutorialQuestion(topic, question, answer, sortOrder);
    return toQuestionResponse(questionRepository.save(entity));
  }

  @Override
  public TutorialQuestionResponse updateQuestion(
      UUID ownerId, UUID questionId, TutorialQuestionRequest request) {
    TutorialQuestion entity =
        questionRepository
            .findByIdAndTopic_OwnerId(questionId, ownerId)
            .orElseThrow(() -> new TutorialQuestionNotFoundException(questionId));
    entity.setQuestion(requireContent(request.question(), MAX_QA_LENGTH, "question"));
    entity.setAnswer(requireContent(request.answer(), MAX_QA_LENGTH, "answer"));
    if (request.sortOrder() != null) {
      if (request.sortOrder() < 0) {
        throw validation("sortOrder", "must not be negative");
      }
      entity.setSortOrder(request.sortOrder());
    }
    return toQuestionResponse(questionRepository.save(entity));
  }

  @Override
  public void deleteQuestion(UUID ownerId, UUID questionId) {
    TutorialQuestion entity =
        questionRepository
            .findByIdAndTopic_OwnerId(questionId, ownerId)
            .orElseThrow(() -> new TutorialQuestionNotFoundException(questionId));
    questionRepository.delete(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public TutorialSearchPageResponse search(UUID ownerId, String query, Pageable pageable) {
    String q = query == null ? "" : query.trim();
    if (q.isBlank()) {
      throw validation("q", "must not be blank");
    }
    int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
    int page = Math.max(pageable.getPageNumber(), 0);
    int offset = page * size;
    long total = searchRepository.count(ownerId, q);
    List<TutorialSearchResultResponse> content =
        searchRepository.search(ownerId, q, size, offset).stream()
            .map(
                hit -> {
                  TutorialTopic topic = requireTopic(ownerId, hit.topicId());
                  return new TutorialSearchResultResponse(
                      hit.topicId(),
                      hit.title(),
                      hit.path(),
                      buildBreadcrumb(topic),
                      sanitizeSnippet(hit.snippet()),
                      mapContentType(hit.contentType()),
                      hit.rank());
                })
            .toList();
    int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
    return new TutorialSearchPageResponse(q, content, page, size, total, totalPages);
  }

  private TutorialTreeNodeResponse toTreeNode(
      TutorialTopic topic, Map<UUID, List<TutorialTopic>> childrenByParent) {
    List<TutorialTopic> children = childrenByParent.getOrDefault(topic.getId(), List.of());
    return new TutorialTreeNodeResponse(
        topic.getId(),
        topic.getTitle(),
        topic.getSlug(),
        topic.getPath(),
        topic.getSortOrder(),
        conceptRepository.existsByTopicId(topic.getId()),
        questionRepository.existsByTopicId(topic.getId()),
        children.stream().map(child -> toTreeNode(child, childrenByParent)).toList());
  }

  private TutorialTopicResponse toTopicResponse(TutorialTopic topic) {
    int childCount =
        topicRepository
            .findByOwnerIdAndParentIdOrderBySortOrderAsc(topic.getOwnerId(), topic.getId())
            .size();
    return new TutorialTopicResponse(
        topic.getId(),
        topic.getParent() == null ? null : topic.getParent().getId(),
        topic.getTitle(),
        topic.getSlug(),
        topic.getPath(),
        topic.getSortOrder(),
        conceptRepository.existsByTopicId(topic.getId()),
        questionRepository.existsByTopicId(topic.getId()),
        childCount,
        buildBreadcrumb(topic),
        topic.getCreatedAt(),
        topic.getUpdatedAt());
  }

  private TutorialQuestionResponse toQuestionResponse(TutorialQuestion question) {
    return new TutorialQuestionResponse(
        question.getId(),
        question.getTopic().getId(),
        question.getQuestion(),
        question.getAnswer(),
        question.getSortOrder(),
        question.getUpdatedAt());
  }

  private List<TutorialBreadcrumbItem> buildBreadcrumb(TutorialTopic topic) {
    List<TutorialTopic> chain = new ArrayList<>();
    TutorialTopic current = topic;
    while (current != null) {
      chain.add(current);
      current = current.getParent();
    }
    List<TutorialBreadcrumbItem> items = new ArrayList<>(chain.size());
    for (int i = chain.size() - 1; i >= 0; i--) {
      TutorialTopic node = chain.get(i);
      items.add(
          new TutorialBreadcrumbItem(
              node.getId(), node.getTitle(), node.getSlug(), node.getPath()));
    }
    return items;
  }

  private TutorialTopic resolveParent(UUID ownerId, UUID parentId) {
    if (parentId == null) {
      return null;
    }
    return requireTopic(ownerId, parentId);
  }

  private TutorialTopic requireTopic(UUID ownerId, UUID topicId) {
    return topicRepository
        .findByIdAndOwnerId(topicId, ownerId)
        .orElseThrow(() -> new TutorialTopicNotFoundException(topicId));
  }

  private TutorialTopic requireTopicByPath(UUID ownerId, String path) {
    return topicRepository
        .findByOwnerIdAndPath(ownerId, path)
        .orElseThrow(() -> new TutorialTopicNotFoundException(path));
  }

  private String resolveSlug(String title, String slug) {
    String resolved =
        slug == null || slug.isBlank()
            ? TutorialSlugger.slugify(title)
            : TutorialSlugger.slugify(slug);
    if (!resolved.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
      throw validation("slug", "must be lowercase kebab-case");
    }
    return resolved;
  }

  private String buildPath(TutorialTopic parent, String slug) {
    return parent == null ? slug : parent.getPath() + "/" + slug;
  }

  private void ensureUniquePath(UUID ownerId, String path, UUID excludeId) {
    if (topicRepository.existsPath(ownerId, path, excludeId)) {
      throw new TutorialDuplicatePathException(path);
    }
  }

  private int resolveSortOrder(UUID ownerId, TutorialTopic parent, Integer requested) {
    if (requested != null) {
      if (requested < 0) {
        throw validation("sortOrder", "must not be negative");
      }
      return requested;
    }
    UUID parentId = parent == null ? null : parent.getId();
    return topicRepository.findMaxSortOrder(ownerId, parentId) + 1;
  }

  private boolean isAncestorOrSelf(TutorialTopic candidateAncestor, TutorialTopic node) {
    TutorialTopic current = node;
    while (current != null) {
      if (current.getId().equals(candidateAncestor.getId())) {
        return true;
      }
      current = current.getParent();
    }
    return false;
  }

  private void repathDescendants(UUID ownerId, String oldPath, String newPath) {
    List<TutorialTopic> descendants = topicRepository.findDescendantsByPathPrefix(ownerId, oldPath);
    for (TutorialTopic descendant : descendants) {
      String suffix = descendant.getPath().substring(oldPath.length());
      descendant.setPath(newPath + suffix);
    }
  }

  private static void validateTitle(String title) {
    if (title == null || title.isBlank()) {
      throw validation("title", "must not be blank");
    }
  }

  private static String requireContent(String value, int max, String field) {
    if (value == null || value.isBlank()) {
      throw validation(field, "must not be blank");
    }
    if (value.length() > max) {
      throw validation(field, "must be at most " + max + " characters");
    }
    return value;
  }

  private static String normalizePath(String path) {
    if (path == null || path.isBlank()) {
      throw validation("path", "must not be blank");
    }
    String normalized = path.trim();
    while (normalized.startsWith("/")) {
      normalized = normalized.substring(1);
    }
    while (normalized.endsWith("/")) {
      normalized = normalized.substring(0, normalized.length() - 1);
    }
    return normalized;
  }

  private static String mapContentType(String raw) {
    if ("CONCEPT".equals(raw)) {
      return "Concept";
    }
    return "Questions & Answers";
  }

  private static String sanitizeSnippet(String snippet) {
    if (snippet == null) {
      return "";
    }
    // Keep mark tags for highlight; strip other tags from ts_headline output.
    return snippet.replaceAll("(?i)</?(?!mark\\b)[a-z][^>]*>", "");
  }

  private static ValidationException validation(String field, String message) {
    return new ValidationException(
        "Request validation failed", List.of(ApiError.FieldErrorDetail.ofField(field, message)));
  }
}
