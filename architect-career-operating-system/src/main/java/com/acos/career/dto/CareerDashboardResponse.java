package com.acos.career.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

/**
 * Aggregated, analytics-ready career tracker dashboard counts.
 *
 * @param totalCompanies total companies
 * @param totalRecruiters total recruiters
 * @param totalActiveApplications total non-archived applications
 * @param applicationsByStatus non-archived application counts by status
 * @param interviewsScheduled scheduled interviews on non-archived applications
 * @param upcomingInterviews upcoming scheduled interviews on non-archived applications
 * @param offersReceived total non-archived offers on non-archived applications
 * @param pendingOffers pending offers on non-archived applications
 * @param acceptedOffers accepted offers on non-archived applications
 * @param rejectedApplications non-archived applications in {@code REJECTED} status
 * @param acceptanceRatio accepted offers divided by decided offers ({@code ACCEPTED} + {@code
 *     DECLINED}); {@code 0} when none decided
 * @param averageInterviewRating average interview rating, or {@code null} when no ratings exist
 */
@Schema(name = "CareerDashboardResponse", description = "Career tracker dashboard summary")
public record CareerDashboardResponse(
    @Schema(
            description = "Total companies",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long totalCompanies,
    @Schema(
            description = "Total recruiters",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long totalRecruiters,
    @Schema(
            description = "Total non-archived applications",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long totalActiveApplications,
    @Schema(
            description = "Non-archived application counts by status",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<ApplicationStatusCountResponse> applicationsByStatus,
    @Schema(
            description = "Scheduled interviews",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long interviewsScheduled,
    @Schema(
            description = "Upcoming scheduled interviews",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long upcomingInterviews,
    @Schema(
            description = "Total offers received",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long offersReceived,
    @Schema(
            description = "Pending offers",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long pendingOffers,
    @Schema(
            description = "Accepted offers",
            example = "0",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long acceptedOffers,
    @Schema(
            description = "Rejected applications",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long rejectedApplications,
    @Schema(
            description = "Acceptance ratio among decided offers",
            example = "0.5",
            requiredMode = Schema.RequiredMode.REQUIRED)
        double acceptanceRatio,
    @Schema(description = "Average interview rating", example = "4.2", nullable = true)
        Double averageInterviewRating) {

  /**
   * Creates an immutable dashboard response.
   *
   * @param totalCompanies total companies
   * @param totalRecruiters total recruiters
   * @param totalActiveApplications total non-archived applications
   * @param applicationsByStatus status counts
   * @param interviewsScheduled scheduled interviews
   * @param upcomingInterviews upcoming interviews
   * @param offersReceived total offers received
   * @param pendingOffers pending offers
   * @param acceptedOffers accepted offers
   * @param rejectedApplications rejected applications
   * @param acceptanceRatio acceptance ratio
   * @param averageInterviewRating average interview rating
   */
  public CareerDashboardResponse {
    Objects.requireNonNull(applicationsByStatus, "applicationsByStatus must not be null");
    applicationsByStatus = List.copyOf(applicationsByStatus);
  }
}
