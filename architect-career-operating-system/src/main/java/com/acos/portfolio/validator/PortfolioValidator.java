package com.acos.portfolio.validator;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import com.acos.portfolio.config.PortfolioProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/** Validates and normalizes portfolio request fields beyond Bean Validation. */
@Component
public class PortfolioValidator {

  private static final int MAX_TECHNOLOGY_NAME_LENGTH = 100;
  private static final BigDecimal MAX_YEARS_OF_EXPERIENCE = new BigDecimal("99.9");

  private final PortfolioProperties properties;

  /**
   * Creates the validator.
   *
   * @param properties portfolio limits
   */
  public PortfolioValidator(PortfolioProperties properties) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
  }

  /**
   * Validates pageable size against configured limits.
   *
   * @param pageable requested pageable
   */
  public void validatePageable(Pageable pageable) {
    Objects.requireNonNull(pageable, "pageable must not be null");
    if (pageable.getPageSize() > properties.maxPageSize()) {
      throw new ValidationException(
          "page size must not exceed " + properties.maxPageSize(),
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "size", "page size must not exceed " + properties.maxPageSize())));
    }
  }

  /**
   * Validates and trims a search query.
   *
   * @param query search text
   * @return trimmed query
   */
  public String normalizeSearchQuery(String query) {
    if (query == null || query.isBlank()) {
      throw new ValidationException(
          "search query must not be blank",
          List.of(ApiError.FieldErrorDetail.ofField("q", "search query must not be blank")));
    }
    return query.trim();
  }

  /**
   * Normalizes optional free-text fields.
   *
   * @param value raw value
   * @return trimmed value, or {@code null} when blank
   */
  public String normalizeOptionalText(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  /**
   * Normalizes technology names by trimming and de-duplicating while preserving case.
   *
   * @param technologyNames raw technology names, may be {@code null}
   * @return normalized unique technology names, or empty when {@code technologyNames} is {@code
   *     null}
   */
  public List<String> normalizeTechnologyNames(List<String> technologyNames) {
    if (technologyNames == null) {
      return List.of();
    }
    Set<String> normalized = new LinkedHashSet<>();
    List<ApiError.FieldErrorDetail> details = new ArrayList<>();
    for (int index = 0; index < technologyNames.size(); index++) {
      normalizeSingleTechnologyName(technologyNames.get(index), index, normalized, details);
    }
    if (!details.isEmpty()) {
      throw new ValidationException("technologyNames validation failed", details);
    }
    if (normalized.size() > properties.maxTechnologiesPerProject()) {
      throw new ValidationException(
          "A project may have at most " + properties.maxTechnologiesPerProject() + " technologies",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "technologyNames",
                  "A project may have at most "
                      + properties.maxTechnologiesPerProject()
                      + " technologies")));
    }
    return List.copyOf(normalized);
  }

  /**
   * Validates project description length against configured limits.
   *
   * @param description project description
   */
  public void validateDescription(String description) {
    Objects.requireNonNull(description, "description must not be null");
    if (description.length() > properties.maxDescriptionLength()) {
      throw new ValidationException(
          "description must not exceed " + properties.maxDescriptionLength() + " characters",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "description",
                  "description must not exceed "
                      + properties.maxDescriptionLength()
                      + " characters")));
    }
  }

  /**
   * Validates that an end date is not before a start date when both are present.
   *
   * @param startDate optional start date
   * @param endDate optional end date
   */
  public void validateDateRange(LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
      throw new ValidationException(
          "endDate must not be before startDate",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "endDate", "endDate must not be before startDate")));
    }
  }

  /**
   * Validates optional years of experience.
   *
   * @param yearsOfExperience optional years value
   */
  public void validateYearsOfExperience(BigDecimal yearsOfExperience) {
    if (yearsOfExperience == null) {
      return;
    }
    if (yearsOfExperience.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationException(
          "yearsOfExperience must not be negative",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "yearsOfExperience", "yearsOfExperience must not be negative")));
    }
    if (yearsOfExperience.compareTo(MAX_YEARS_OF_EXPERIENCE) > 0) {
      throw new ValidationException(
          "yearsOfExperience must not exceed " + MAX_YEARS_OF_EXPERIENCE,
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "yearsOfExperience",
                  "yearsOfExperience must not exceed " + MAX_YEARS_OF_EXPERIENCE)));
    }
  }

  /**
   * Validates that a certification expiry is not before the issue date.
   *
   * @param issuedOn issue date
   * @param expiresOn optional expiry date
   */
  public void validateCertificationDates(LocalDate issuedOn, LocalDate expiresOn) {
    Objects.requireNonNull(issuedOn, "issuedOn must not be null");
    if (expiresOn != null && expiresOn.isBefore(issuedOn)) {
      throw new ValidationException(
          "expiresOn must not be before issuedOn",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "expiresOn", "expiresOn must not be before issuedOn")));
    }
  }

  private void normalizeSingleTechnologyName(
      String raw, int index, Set<String> normalized, List<ApiError.FieldErrorDetail> details) {
    if (raw == null || raw.isBlank()) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              "technologyNames[" + index + "]", "technology name must not be blank"));
      return;
    }
    String value = raw.trim();
    if (value.length() > MAX_TECHNOLOGY_NAME_LENGTH) {
      details.add(
          ApiError.FieldErrorDetail.ofField(
              "technologyNames[" + index + "]",
              "technology name must not exceed " + MAX_TECHNOLOGY_NAME_LENGTH + " characters"));
      return;
    }
    normalized.add(value);
  }
}
