package com.acos.career.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.career.config.CareerProperties;
import com.acos.common.exception.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

/** Unit tests for {@link CareerValidator}. */
class CareerValidatorTest {

  private CareerValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CareerValidator(new CareerProperties(50, 100, 200));
  }

  @Test
  void shouldNormalizeBlankOptionalTextToNull() {
    assertThat(validator.normalizeOptionalText("  ")).isNull();
    assertThat(validator.normalizeOptionalText(" Backend ")).isEqualTo("Backend");
  }

  @Test
  void shouldNormalizeCurrencyToUpperCase() {
    assertThat(validator.normalizeCurrency(" usd ")).isEqualTo("USD");
    assertThat(validator.normalizeCurrency(null)).isNull();
  }

  @Test
  void shouldRejectOversizedPage() {
    assertThatThrownBy(() -> validator.validatePageable(PageRequest.of(0, 51)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("page size");
  }

  @Test
  void shouldRejectOversizedNotes() {
    assertThatThrownBy(() -> validator.validateNotesLength("x".repeat(101)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("notes");
  }

  @Test
  void shouldRejectOversizedJobDescription() {
    assertThatThrownBy(() -> validator.validateJobDescriptionLength("x".repeat(201)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("jobDescription");
  }

  @Test
  void shouldRejectNegativeAmount() {
    assertThatThrownBy(() -> validator.validateAmount("baseSalary", new BigDecimal("-1")))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("baseSalary");
  }

  @Test
  void shouldRejectRatingOutOfRange() {
    assertThatThrownBy(() -> validator.validateRating("rating", 6))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("rating");
    assertThatThrownBy(() -> validator.validateRating("rating", 0))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("rating");
  }

  @Test
  void shouldRejectNegativeNoticePeriod() {
    assertThatThrownBy(() -> validator.validateNoticePeriodDays(-5))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("noticePeriodDays");
  }

  @Test
  void shouldRejectNonPositiveDuration() {
    assertThatThrownBy(() -> validator.validateDurationMinutes(0))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("durationMinutes");
  }
}
