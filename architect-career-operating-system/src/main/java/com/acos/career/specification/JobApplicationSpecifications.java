package com.acos.career.specification;

import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.Interview;
import com.acos.career.entity.InterviewRound;
import com.acos.career.entity.JobApplication;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

/** Composable {@link Specification} filters for {@link JobApplication} search queries. */
public final class JobApplicationSpecifications {

  private JobApplicationSpecifications() {}

  /**
   * Filters applications owned by a given user.
   *
   * @param ownerId owner id
   * @return ownership filter
   */
  public static Specification<JobApplication> ownedBy(UUID ownerId) {
    return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
  }

  /**
   * Filters non-archived applications.
   *
   * @return non-archived filter
   */
  public static Specification<JobApplication> notArchived() {
    return archivedOnly(false);
  }

  /**
   * Filters applications by archival state.
   *
   * @param includeArchived {@code true} to return only archived applications, {@code false} to
   *     return only active applications
   * @return archival filter
   */
  public static Specification<JobApplication> archivedOnly(boolean includeArchived) {
    return (root, query, cb) -> cb.equal(root.get("archived"), includeArchived);
  }

  /**
   * Filters applications by target company.
   *
   * @param companyId company id, or {@code null} to skip filtering
   * @return company filter, or {@code null} when {@code companyId} is {@code null}
   */
  public static Specification<JobApplication> companyId(UUID companyId) {
    if (companyId == null) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
  }

  /**
   * Filters applications by associated recruiter.
   *
   * @param recruiterId recruiter id, or {@code null} to skip filtering
   * @return recruiter filter, or {@code null} when {@code recruiterId} is {@code null}
   */
  public static Specification<JobApplication> recruiterId(UUID recruiterId) {
    if (recruiterId == null) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("recruiter").get("id"), recruiterId);
  }

  /**
   * Filters applications by status.
   *
   * @param status status, or {@code null} to skip filtering
   * @return status filter, or {@code null} when {@code status} is {@code null}
   */
  public static Specification<JobApplication> status(ApplicationStatus status) {
    if (status == null) {
      return null;
    }
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  /**
   * Filters applications having at least one interview of the given round.
   *
   * @param interviewRound interview round, or {@code null} to skip filtering
   * @return interview round filter, or {@code null} when {@code interviewRound} is {@code null}
   */
  public static Specification<JobApplication> interviewRound(InterviewRound interviewRound) {
    if (interviewRound == null) {
      return null;
    }
    return (root, query, cb) -> {
      query.distinct(true);
      Join<JobApplication, Interview> interviews = root.join("interviews", JoinType.INNER);
      return cb.equal(interviews.get("interviewRound"), interviewRound);
    };
  }

  /**
   * Filters applications applied within an inclusive date range.
   *
   * @param appliedFrom lower bound, or {@code null} for no lower bound
   * @param appliedTo upper bound, or {@code null} for no upper bound
   * @return applied-on range filter, or {@code null} when both bounds are {@code null}
   */
  public static Specification<JobApplication> appliedBetween(
      LocalDate appliedFrom, LocalDate appliedTo) {
    if (appliedFrom == null && appliedTo == null) {
      return null;
    }
    if (appliedFrom != null && appliedTo != null) {
      return (root, query, cb) -> cb.between(root.get("appliedOn"), appliedFrom, appliedTo);
    }
    if (appliedFrom != null) {
      return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("appliedOn"), appliedFrom);
    }
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("appliedOn"), appliedTo);
  }

  /**
   * Filters applications with a salary expectation within an inclusive range.
   *
   * @param salaryMin lower bound, or {@code null} for no lower bound
   * @param salaryMax upper bound, or {@code null} for no upper bound
   * @return salary range filter, or {@code null} when both bounds are {@code null}
   */
  public static Specification<JobApplication> salaryBetween(
      BigDecimal salaryMin, BigDecimal salaryMax) {
    if (salaryMin == null && salaryMax == null) {
      return null;
    }
    if (salaryMin != null && salaryMax != null) {
      return (root, query, cb) -> cb.between(root.get("salaryExpectation"), salaryMin, salaryMax);
    }
    if (salaryMin != null) {
      return (root, query, cb) -> cb.ge(root.get("salaryExpectation"), salaryMin);
    }
    return (root, query, cb) -> cb.le(root.get("salaryExpectation"), salaryMax);
  }

  /**
   * Filters applications by a case-insensitive keyword matched against title, job description, and
   * notes.
   *
   * @param keyword keyword text, or {@code null}/blank to skip filtering
   * @return keyword filter, or {@code null} when {@code keyword} is blank
   */
  public static Specification<JobApplication> keyword(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }
    String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
    return (root, query, cb) ->
        cb.or(
            cb.like(cb.lower(root.get("title")), pattern),
            cb.like(cb.lower(cb.coalesce(root.get("jobDescription"), "")), pattern),
            cb.like(cb.lower(cb.coalesce(root.get("notes"), "")), pattern));
  }

  /**
   * Combines specifications with logical AND, skipping {@code null} entries.
   *
   * @param specifications specifications to combine, any of which may be {@code null}
   * @return combined specification
   */
  @SafeVarargs
  public static Specification<JobApplication> and(Specification<JobApplication>... specifications) {
    Specification<JobApplication> combined = Specification.where(null);
    for (Specification<JobApplication> specification : specifications) {
      if (specification != null) {
        combined = combined.and(specification);
      }
    }
    return combined;
  }
}
