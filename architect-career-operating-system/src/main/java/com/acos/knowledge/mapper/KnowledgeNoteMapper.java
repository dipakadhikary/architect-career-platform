package com.acos.knowledge.mapper;

import com.acos.knowledge.dto.CategoryResponse;
import com.acos.knowledge.dto.KnowledgeNotePageResponse;
import com.acos.knowledge.dto.KnowledgeNoteResponse;
import com.acos.knowledge.entity.Category;
import com.acos.knowledge.entity.KnowledgeNote;
import com.acos.knowledge.entity.Tag;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

/** MapStruct mappings between knowledge domain objects and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface KnowledgeNoteMapper {

  /**
   * Maps a persisted note to an API response.
   *
   * @param note persisted note with category and tags initialized
   * @return knowledge note response
   */
  @Mapping(target = "category", source = "category")
  @Mapping(target = "tags", source = "tags", qualifiedByName = "toTagNames")
  KnowledgeNoteResponse toResponse(KnowledgeNote note);

  /**
   * Maps a category entity to a response DTO.
   *
   * @param category category entity, may be {@code null}
   * @return category response, or {@code null}
   */
  CategoryResponse toCategoryResponse(Category category);

  /**
   * Maps a page of notes to a page response.
   *
   * @param page Spring Data page
   * @return page response
   */
  default KnowledgeNotePageResponse toPageResponse(Page<KnowledgeNote> page) {
    List<KnowledgeNoteResponse> content = page.getContent().stream().map(this::toResponse).toList();
    return new KnowledgeNotePageResponse(
        content,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  /**
   * Maps tags to sorted unique names.
   *
   * @param tags tag set
   * @return sorted tag names
   */
  @Named("toTagNames")
  default List<String> toTagNames(Set<Tag> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    return tags.stream().map(Tag::getName).sorted(Comparator.naturalOrder()).toList();
  }
}
