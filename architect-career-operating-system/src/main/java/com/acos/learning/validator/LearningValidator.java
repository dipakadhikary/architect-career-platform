package com.acos.learning.validator;

import com.acos.common.api.ApiError;
import com.acos.common.exception.ValidationException;
import com.acos.learning.config.LearningProperties;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/** Validates learning request constraints beyond Bean Validation. */
@Component
public class LearningValidator {

  private final LearningProperties properties;

  /**
   * Creates the validator.
   *
   * @param properties learning limits
   */
  public LearningValidator(LearningProperties properties) {
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
   * Ensures a plan can accept another milestone.
   *
   * @param currentCount current milestone count
   */
  public void validateMilestoneCapacity(long currentCount) {
    if (currentCount >= properties.maxMilestonesPerPlan()) {
      throw new ValidationException(
          "A plan may have at most " + properties.maxMilestonesPerPlan() + " milestones",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "milestones",
                  "A plan may have at most " + properties.maxMilestonesPerPlan() + " milestones")));
    }
  }

  /**
   * Ensures a milestone can accept another topic.
   *
   * @param currentCount current topic count
   */
  public void validateTopicCapacity(long currentCount) {
    if (currentCount >= properties.maxTopicsPerMilestone()) {
      throw new ValidationException(
          "A milestone may have at most " + properties.maxTopicsPerMilestone() + " topics",
          List.of(
              ApiError.FieldErrorDetail.ofField(
                  "topics",
                  "A milestone may have at most "
                      + properties.maxTopicsPerMilestone()
                      + " topics")));
    }
  }

  /**
   * Normalizes an optional description.
   *
   * @param description raw description
   * @return trimmed description, or {@code null} when blank
   */
  public String normalizeDescription(String description) {
    if (description == null || description.isBlank()) {
      return null;
    }
    return description.trim();
  }

  /**
   * Resolves sort order for create operations.
   *
   * @param requested requested sort order, may be {@code null}
   * @param maxExisting maximum existing sort order, may be empty
   * @return resolved sort order
   */
  public int resolveSortOrder(Integer requested, java.util.Optional<Integer> maxExisting) {
    if (requested != null) {
      if (requested < 0) {
        throw new ValidationException(
            "sortOrder must not be negative",
            List.of(
                ApiError.FieldErrorDetail.ofField("sortOrder", "sortOrder must not be negative")));
      }
      return requested;
    }
    return maxExisting.map(value -> value + 1).orElse(0);
  }
}
