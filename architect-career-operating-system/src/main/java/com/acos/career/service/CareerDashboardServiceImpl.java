package com.acos.career.service;

import com.acos.career.dto.ApplicationStatusCountResponse;
import com.acos.career.dto.CareerDashboardResponse;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.OfferStatus;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.InterviewRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.OfferRepository;
import com.acos.career.repository.RecruiterRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link CareerDashboardService} implementation. */
@Service
@Transactional(readOnly = true)
public class CareerDashboardServiceImpl implements CareerDashboardService {

  private final CompanyRepository companyRepository;
  private final RecruiterRepository recruiterRepository;
  private final JobApplicationRepository jobApplicationRepository;
  private final InterviewRepository interviewRepository;
  private final OfferRepository offerRepository;

  /**
   * Creates the career dashboard service.
   *
   * @param companyRepository company repository
   * @param recruiterRepository recruiter repository
   * @param jobApplicationRepository application repository
   * @param interviewRepository interview repository
   * @param offerRepository offer repository
   */
  public CareerDashboardServiceImpl(
      CompanyRepository companyRepository,
      RecruiterRepository recruiterRepository,
      JobApplicationRepository jobApplicationRepository,
      InterviewRepository interviewRepository,
      OfferRepository offerRepository) {
    this.companyRepository =
        Objects.requireNonNull(companyRepository, "companyRepository must not be null");
    this.recruiterRepository =
        Objects.requireNonNull(recruiterRepository, "recruiterRepository must not be null");
    this.jobApplicationRepository =
        Objects.requireNonNull(
            jobApplicationRepository, "jobApplicationRepository must not be null");
    this.interviewRepository =
        Objects.requireNonNull(interviewRepository, "interviewRepository must not be null");
    this.offerRepository =
        Objects.requireNonNull(offerRepository, "offerRepository must not be null");
  }

  @Override
  public CareerDashboardResponse getSummary(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");

    Map<ApplicationStatus, Long> statusCounts = new EnumMap<>(ApplicationStatus.class);
    for (Object[] row : jobApplicationRepository.countGroupedByStatusForOwner(ownerId)) {
      statusCounts.put((ApplicationStatus) row[0], (Long) row[1]);
    }

    List<ApplicationStatusCountResponse> applicationsByStatus =
        Arrays.stream(ApplicationStatus.values())
            .map(
                status ->
                    new ApplicationStatusCountResponse(
                        status, statusCounts.getOrDefault(status, 0L)))
            .toList();

    long rejectedApplications = statusCounts.getOrDefault(ApplicationStatus.REJECTED, 0L);
    long acceptedOffers =
        offerRepository.countByOwnerIdAndOfferStatus(ownerId, OfferStatus.ACCEPTED);
    long declinedOffers =
        offerRepository.countByOwnerIdAndOfferStatus(ownerId, OfferStatus.DECLINED);
    long decidedOffers = acceptedOffers + declinedOffers;
    double acceptanceRatio = decidedOffers == 0L ? 0.0d : (double) acceptedOffers / decidedOffers;

    return new CareerDashboardResponse(
        companyRepository.countByOwnerIdAndArchivedFalse(ownerId),
        recruiterRepository.countByOwnerIdAndArchivedFalse(ownerId),
        jobApplicationRepository.countByOwnerIdAndArchivedFalse(ownerId),
        applicationsByStatus,
        interviewRepository.countScheduledByOwnerId(ownerId),
        interviewRepository.countUpcomingByOwnerId(ownerId, Instant.now()),
        offerRepository.countByOwnerId(ownerId),
        offerRepository.countByOwnerIdAndOfferStatus(ownerId, OfferStatus.PENDING),
        acceptedOffers,
        rejectedApplications,
        acceptanceRatio,
        interviewRepository.averageRatingByOwnerId(ownerId));
  }
}
