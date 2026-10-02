package com.acos.portfolio.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.acos.common.exception.ValidationException;
import com.acos.portfolio.config.PortfolioProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

/** Unit tests for {@link PortfolioValidator}. */
class PortfolioValidatorTest {

  private PortfolioValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PortfolioValidator(new PortfolioProperties(50, 3, 100));
  }

  @Test
  void shouldNormalizeTechnologyNamesAndDeduplicatePreservingCase() {
    assertThat(validator.normalizeTechnologyNames(List.of(" Java ", "Spring Boot", "Java")))
        .containsExactly("Java", "Spring Boot");
  }

  @Test
  void shouldRejectTooManyTechnologies() {
    assertThatThrownBy(
            () -> validator.normalizeTechnologyNames(List.of("one", "two", "three", "four")))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("at most");
  }

  @Test
  void shouldNormalizeBlankOptionalTextToNull() {
    assertThat(validator.normalizeOptionalText("  ")).isNull();
    assertThat(validator.normalizeOptionalText(" Backend ")).isEqualTo("Backend");
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

  @Test
  void shouldRejectOversizedDescription() {
    assertThatThrownBy(() -> validator.validateDescription("x".repeat(101)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("description");
  }

  @Test
  void shouldRejectInvalidDateRange() {
    assertThatThrownBy(
            () -> validator.validateDateRange(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("endDate");
  }

  @Test
  void shouldRejectNegativeYearsOfExperience() {
    assertThatThrownBy(() -> validator.validateYearsOfExperience(new BigDecimal("-1")))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("yearsOfExperience");
  }

  @Test
  void shouldRejectCertificationExpiryBeforeIssue() {
    assertThatThrownBy(
            () ->
                validator.validateCertificationDates(
                    LocalDate.of(2026, 5, 1), LocalDate.of(2025, 5, 1)))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("expiresOn");
  }
}
