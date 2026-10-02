package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.acos.career.dto.CareerDashboardResponse;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.OfferStatus;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.InterviewRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.OfferRepository;
import com.acos.career.repository.RecruiterRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link CareerDashboardServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class CareerDashboardServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Mock private CompanyRepository companyRepository;
  @Mock private RecruiterRepository recruiterRepository;
  @Mock private JobApplicationRepository jobApplicationRepository;
  @Mock private InterviewRepository interviewRepository;
  @Mock private OfferRepository offerRepository;

  private CareerDashboardServiceImpl careerDashboardService;

  @BeforeEach
  void setUp() {
    careerDashboardService =
        new CareerDashboardServiceImpl(
            companyRepository,
            recruiterRepository,
            jobApplicationRepository,
            interviewRepository,
            offerRepository);
  }

  @Test
  void shouldAggregateDashboardCounts() {
    when(companyRepository.countByOwnerIdAndArchivedFalse(OWNER_ID)).thenReturn(5L);
    when(recruiterRepository.countByOwnerIdAndArchivedFalse(OWNER_ID)).thenReturn(3L);
    when(jobApplicationRepository.countByOwnerIdAndArchivedFalse(OWNER_ID)).thenReturn(12L);
    when(jobApplicationRepository.countGroupedByStatusForOwner(OWNER_ID))
        .thenReturn(
            List.of(
                new Object[] {ApplicationStatus.APPLIED, 4L},
                new Object[] {ApplicationStatus.REJECTED, 2L}));
    when(interviewRepository.countScheduledByOwnerId(OWNER_ID)).thenReturn(3L);
    when(interviewRepository.countUpcomingByOwnerId(eq(OWNER_ID), any(Instant.class)))
        .thenReturn(2L);
    when(interviewRepository.averageRatingByOwnerId(OWNER_ID)).thenReturn(4.2d);
    when(offerRepository.countByOwnerId(OWNER_ID)).thenReturn(2L);
    when(offerRepository.countByOwnerIdAndOfferStatus(OWNER_ID, OfferStatus.PENDING))
        .thenReturn(1L);
    when(offerRepository.countByOwnerIdAndOfferStatus(OWNER_ID, OfferStatus.ACCEPTED))
        .thenReturn(1L);
    when(offerRepository.countByOwnerIdAndOfferStatus(OWNER_ID, OfferStatus.DECLINED))
        .thenReturn(1L);

    CareerDashboardResponse response = careerDashboardService.getSummary(OWNER_ID);

    assertThat(response.totalCompanies()).isEqualTo(5L);
    assertThat(response.totalRecruiters()).isEqualTo(3L);
    assertThat(response.totalActiveApplications()).isEqualTo(12L);
    assertThat(response.interviewsScheduled()).isEqualTo(3L);
    assertThat(response.upcomingInterviews()).isEqualTo(2L);
    assertThat(response.offersReceived()).isEqualTo(2L);
    assertThat(response.pendingOffers()).isEqualTo(1L);
    assertThat(response.acceptedOffers()).isEqualTo(1L);
    assertThat(response.rejectedApplications()).isEqualTo(2L);
    assertThat(response.acceptanceRatio()).isEqualTo(0.5d);
    assertThat(response.averageInterviewRating()).isEqualTo(4.2d);
    assertThat(response.applicationsByStatus()).hasSize(ApplicationStatus.values().length);
    assertThat(response.applicationsByStatus())
        .anyMatch(count -> count.status() == ApplicationStatus.APPLIED && count.count() == 4L);
    assertThat(response.applicationsByStatus())
        .anyMatch(count -> count.status() == ApplicationStatus.REJECTED && count.count() == 2L);
  }
}
