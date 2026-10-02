package com.acos.career.validator;

import com.acos.career.config.CareerProperties;
import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/** Validates and normalizes career request fields beyond Bean Validation. */
@Component
public class CareerValidator {

  private static final int MIN_RATING = 1;
  private static final int MAX_RATING = 5;

  private final CareerProperties properties;

  /**
   * Creates the validator.
   *
   * @param properties career limits
   */
  public CareerValidator(CareerProperties properties) {
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
   * Normalizes a required free-text field.
   *
   * @param value raw value
   * @return trimmed value
   */
  public String normalizeRequiredText(String value) {
    Objects.requireNonNull(value, "value must not be null");
    return value.trim();
  }

  /**
   * Normalizes an optional currency code to uppercase using {@link Locale#ROOT}.
   *
   * @param currency raw currency code
   * @return normalized currency code, or {@code null} when blank
   */
  public String normalizeCurrency(String currency) {
    String normalized = normalizeOptionalText(currency);
    return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
  }

  /**
   * Validates notes length against configured limits.
   *
   * @param notes optional notes
   */
  public void validateNotesLength(String notes) {
    if (notes == null) {
      return;
    }
    if (notes.length() > properties.maxNotesLength()) {
      throw new ValidationException(
          "notes must not exceed " + properties.maxNotesLength() + " characters",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "notes",
                  "notes must not exceed " + properties.maxNotesLength() + " characters")));
    }
  }

  /**
   * Validates job description length against configured limits.
   *
   * @param jobDescription optional job description
   */
  public void validateJobDescriptionLength(String jobDescription) {
    if (jobDescription == null) {
      return;
    }
    if (jobDescription.length() > properties.maxJobDescriptionLength()) {
      throw new ValidationException(
          "jobDescription must not exceed " + properties.maxJobDescriptionLength() + " characters",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "jobDescription",
                  "jobDescription must not exceed "
                      + properties.maxJobDescriptionLength()
                      + " characters")));
    }
  }

  /**
   * Validates that an optional monetary amount is non-negative.
   *
   * @param fieldName field name for error reporting
   * @param amount optional monetary amount
   */
  public void validateAmount(String fieldName, BigDecimal amount) {
    Objects.requireNonNull(fieldName, "fieldName must not be null");
    if (amount == null) {
      return;
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationException(
          fieldName + " must not be negative",
          List.of(
              ApiError.FieldErrorDetail.ofField(fieldName, fieldName + " must not be negative")));
    }
  }

  /**
   * Validates that an optional rating falls within the inclusive 1-5 range.
   *
   * @param fieldName field name for error reporting
   * @param rating optional rating
   */
  public void validateRating(String fieldName, Integer rating) {
    Objects.requireNonNull(fieldName, "fieldName must not be null");
    if (rating == null) {
      return;
    }
    if (rating < MIN_RATING || rating > MAX_RATING) {
      throw new ValidationException(
          fieldName + " must be between " + MIN_RATING + " and " + MAX_RATING,
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  fieldName, fieldName + " must be between " + MIN_RATING + " and " + MAX_RATING)));
    }
  }

  /**
   * Validates that an optional notice period, in days, is non-negative.
   *
   * @param noticePeriodDays optional notice period in days
   */
  public void validateNoticePeriodDays(Integer noticePeriodDays) {
    if (noticePeriodDays == null) {
      return;
    }
    if (noticePeriodDays < 0) {
      throw new ValidationException(
          "noticePeriodDays must not be negative",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "noticePeriodDays", "noticePeriodDays must not be negative")));
    }
  }

  /**
   * Validates that an optional duration, in minutes, is positive.
   *
   * @param durationMinutes optional duration in minutes
   */
  public void validateDurationMinutes(Integer durationMinutes) {
    if (durationMinutes == null) {
      return;
    }
    if (durationMinutes <= 0) {
      throw new ValidationException(
          "durationMinutes must be positive",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "durationMinutes", "durationMinutes must be positive")));
    }
  }

  /**
   * Requires a non-blank resume version identifier.
   *
   * @param resumeVersion resume version identifier
   * @return trimmed resume version
   */
  public String requireResumeVersion(String resumeVersion) {
    String normalized = normalizeOptionalText(resumeVersion);
    if (normalized == null) {
      throw new ValidationException(
          "resumeVersion must not be blank",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "resumeVersion", "resumeVersion must not be blank")));
    }
    return normalized;
  }

  /**
   * Validates that an interview date is not in the past when scheduling.
   *
   * @param interviewDate scheduled interview instant
   * @param now reference instant
   */
  public void validateInterviewDateNotInPast(Instant interviewDate, Instant now) {
    Objects.requireNonNull(interviewDate, "interviewDate must not be null");
    Objects.requireNonNull(now, "now must not be null");
    if (interviewDate.isBefore(now)) {
      throw new ValidationException(
          "interviewDate must not be in the past when scheduling",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "interviewDate", "interviewDate must not be in the past when scheduling")));
    }
  }

  /**
   * Validates that a joining date is strictly after the offer creation date (UTC).
   *
   * @param joiningDate optional joining date
   * @param offerCreatedAt offer creation instant
   */
  public void validateJoiningDateAfterOfferCreation(LocalDate joiningDate, Instant offerCreatedAt) {
    if (joiningDate == null) {
      return;
    }
    Objects.requireNonNull(offerCreatedAt, "offerCreatedAt must not be null");
    LocalDate createdOn = LocalDate.ofInstant(offerCreatedAt, ZoneOffset.UTC);
    if (!joiningDate.isAfter(createdOn)) {
      throw new ValidationException(
          "joiningDate must be after offer creation date",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "joiningDate", "joiningDate must be after offer creation date")));
    }
  }
}
