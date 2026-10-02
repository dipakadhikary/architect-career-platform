package com.acos.career.state;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.career.entity.ApplicationStatus;
import com.acos.career.exception.InvalidApplicationStatusTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link ApplicationStateValidator}. */
class ApplicationStateValidatorTest {

  private ApplicationStateValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ApplicationStateValidator();
  }

  @Test
  void shouldAllowValidTransition() {
    assertThatCode(
            () -> validator.validateTransition(ApplicationStatus.DRAFT, ApplicationStatus.APPLIED))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectInvalidTransition() {
    assertThatThrownBy(
            () -> validator.validateTransition(ApplicationStatus.DRAFT, ApplicationStatus.OFFER))
        .isInstanceOf(InvalidApplicationStatusTransitionException.class)
        .hasMessageContaining("DRAFT")
        .hasMessageContaining("OFFER");
  }

  @Test
  void shouldRejectAnyTransitionFromAccepted() {
    for (ApplicationStatus target : ApplicationStatus.values()) {
      assertThatThrownBy(() -> validator.validateTransition(ApplicationStatus.ACCEPTED, target))
          .isInstanceOf(InvalidApplicationStatusTransitionException.class);
    }
  }
}
