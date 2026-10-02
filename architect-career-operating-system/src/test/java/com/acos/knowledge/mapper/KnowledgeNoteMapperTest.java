package com.acos.knowledge.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.knowledge.entity.Tag;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** Unit tests for {@link KnowledgeNoteMapper}. */
class KnowledgeNoteMapperTest {

  private KnowledgeNoteMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(KnowledgeNoteMapper.class);
  }

  @Test
  void shouldMapTagsToSortedNames() {
    UUID ownerId = UUID.randomUUID();
    Set<Tag> tags =
        Set.of(new Tag(ownerId, "zeta"), new Tag(ownerId, "alpha"), new Tag(ownerId, "middle"));

    assertThat(mapper.toTagNames(tags)).containsExactly("alpha", "middle", "zeta");
  }

  @Test
  void shouldMapNullOrEmptyTagsToEmptyList() {
    assertThat(mapper.toTagNames(null)).isEmpty();
    assertThat(mapper.toTagNames(Set.of())).isEmpty();
  }

  @Test
  void shouldMapNullCategoryToNull() {
    assertThat(mapper.toCategoryResponse(null)).isNull();
  }
}
