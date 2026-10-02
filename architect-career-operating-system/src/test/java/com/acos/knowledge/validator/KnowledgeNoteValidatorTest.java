package com.acos.knowledge.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.common.exception.ValidationException;
import com.acos.knowledge.config.KnowledgeProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

/** Unit tests for {@link KnowledgeNoteValidator}. */
class KnowledgeNoteValidatorTest {

  private KnowledgeNoteValidator validator;

  @BeforeEach
  void setUp() {
    validator = new KnowledgeNoteValidator(new KnowledgeProperties(100, 3, 50));
  }

  @Test
  void shouldAcceptContentWithinLimit() {
    validator.validateContent("x".repeat(100));
  }

  @Test
  void shouldRejectOversizedContent() {
    assertThatThrownBy(() -> validator.validateContent("x".repeat(101)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("content");
  }

  @Test
  void shouldNormalizeTagNamesAndDeduplicate() {
    assertThat(validator.normalizeTagNames(List.of(" Interview ", "DISTRIBUTED", "interview")))
        .containsExactlyInAnyOrder("interview", "distributed");
  }

  @Test
  void shouldRejectTooManyTags() {
    assertThatThrownBy(() -> validator.normalizeTagNames(List.of("one", "two", "three", "four")))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("at most");
  }

  @Test
  void shouldNormalizeBlankCategoryToNull() {
    assertThat(validator.normalizeCategoryName("  ")).isNull();
    assertThat(validator.normalizeCategoryName(" System Design ")).isEqualTo("System Design");
  }

  @Test
  void shouldRejectBlankSearchQuery() {
    assertThatThrownBy(() -> validator.normalizeSearchQuery("   "))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("search query");
  }

  @Test
  void shouldRejectOversizedPage() {
    assertThatThrownBy(() -> validator.validatePageable(PageRequest.of(0, 51)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("page size");
  }
}
