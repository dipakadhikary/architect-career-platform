package com.acos.knowledge.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link KnowledgeProperties}. */
class KnowledgePropertiesTest {

  @Test
  void shouldAcceptPositiveLimits() {
    KnowledgeProperties properties = new KnowledgeProperties(1000, 10, 50);

    assertThat(properties.maxContentLength()).isEqualTo(1000);
    assertThat(properties.maxTagsPerNote()).isEqualTo(10);
    assertThat(properties.maxPageSize()).isEqualTo(50);
  }

  @Test
  void shouldRejectNonPositiveContentLength() {
    assertThatThrownBy(() -> new KnowledgeProperties(0, 10, 50))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxContentLength");
  }

  @Test
  void shouldRejectNonPositiveTagsPerNote() {
    assertThatThrownBy(() -> new KnowledgeProperties(1000, 0, 50))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxTagsPerNote");
  }

  @Test
  void shouldRejectNonPositivePageSize() {
    assertThatThrownBy(() -> new KnowledgeProperties(1000, 10, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("maxPageSize");
  }
}
