package com.acos.tutorial.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TutorialSluggerTest {

  @Test
  void shouldSlugifyTitle() {
    assertThat(TutorialSlugger.slugify("Java Design Patterns")).isEqualTo("java-design-patterns");
  }

  @Test
  void shouldRejectBlankDerivedSlug() {
    assertThatThrownBy(() -> TutorialSlugger.slugify("@@@"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
