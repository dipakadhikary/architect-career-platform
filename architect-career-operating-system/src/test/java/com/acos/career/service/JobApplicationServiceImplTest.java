package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.career.dto.CompanySummaryResponse;
import com.acos.career.dto.JobApplicationRequest;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.StatusTransitionRequest;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.ApplicationStatusHistory;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.Company;
import com.acos.career.entity.JobApplication;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.exception.ApplicationArchivedException;
import com.acos.career.exception.InvalidApplicationStatusTransitionException;
import com.acos.career.exception.JobApplicationNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.ApplicationStatusHistoryRepository;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.RecruiterRepository;
import com.acos.career.state.ApplicationStateValidator;
import com.acos.career.validator.CareerValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link JobApplicationServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID APPLICATION_ID =
      UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final UUID COMPANY_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

  @Mock private JobApplicationRepository jobApplicationRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private RecruiterRepository recruiterRepository;
  @Mock private ApplicationStatusHistoryRepository applicationStatusHistoryRepository;
  @Mock private CareerMapper careerMapper;
  @Mock private CareerValidator careerValidator;
  @Mock private ApplicationStateValidator applicationStateValidator;
  @Mock private CareerAuditService careerAuditService;
  @Mock private CareerDomainEventPublisher careerDomainEventPublisher;

  private JobApplicationServiceImpl jobApplicationService;

  @BeforeEach
  void setUp() {
    jobApplicationService =
        new JobApplicationServiceImpl(
            jobApplicationRepository,
            companyRepository,
            recruiterRepository,
            applicationStatusHistoryRepository,
            careerMapper,
            careerValidator,
            applicationStateValidator,
            careerAuditService,
            careerDomainEventPublisher);
  }

  @Test
  void shouldFailWhenApplicationMissing() {
    when(jobApplicationRepository.findWithDetailsByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> jobApplicationService.get(OWNER_ID, APPLICATION_ID))
        .isInstanceOf(JobApplicationNotFoundException.class)
        .hasMessageContaining(APPLICATION_ID.toString());
  }

  @Test
  void shouldCreateApplicationInDraftStatusAndRecordHistory() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplicationRequest request =
        new JobApplicationRequest(
            COMPANY_ID,
            null,
            "Staff Engineer",
            null,
            null,
            null,
            null,
            "v3-architect",
            LocalDate.of(2026, 8, 1),
            null,
            null,
            null);

    when(careerValidator.normalizeRequiredText("Staff Engineer")).thenReturn("Staff Engineer");
    when(careerValidator.requireResumeVersion("v3-architect")).thenReturn("v3-architect");
    when(companyRepository.findByIdAndOwnerIdAndArchivedFalse(COMPANY_ID, OWNER_ID))
        .thenReturn(Optional.of(company));
    when(jobApplicationRepository.save(any(JobApplication.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(careerMapper.toJobApplicationResponse(any(JobApplication.class)))
        .thenReturn(sampleResponse(ApplicationStatus.DRAFT));

    JobApplicationResponse response = jobApplicationService.create(OWNER_ID, request);

    assertThat(response.status()).isEqualTo(ApplicationStatus.DRAFT);
    ArgumentCaptor<JobApplication> applicationCaptor =
        ArgumentCaptor.forClass(JobApplication.class);
    verify(jobApplicationRepository).save(applicationCaptor.capture());
    assertThat(applicationCaptor.getValue().getStatus()).isEqualTo(ApplicationStatus.DRAFT);

    ArgumentCaptor<ApplicationStatusHistory> historyCaptor =
        ArgumentCaptor.forClass(ApplicationStatusHistory.class);
    verify(applicationStatusHistoryRepository).save(historyCaptor.capture());
    assertThat(historyCaptor.getValue().getOldStatus()).isNull();
    assertThat(historyCaptor.getValue().getNewStatus()).isEqualTo(ApplicationStatus.DRAFT);
    verify(careerAuditService)
        .record(
            OWNER_ID,
            OWNER_ID,
            CareerAuditAction.APPLICATION_CREATED,
            "JobApplication",
            null,
            null);
  }

  @Test
  void shouldTransitionStatusAndRecordHistoryAndAudit() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));
    ReflectionTestUtils.setField(application, "id", APPLICATION_ID);
    StatusTransitionRequest request = new StatusTransitionRequest(ApplicationStatus.APPLIED, null);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    when(careerMapper.toJobApplicationResponse(application))
        .thenReturn(sampleResponse(ApplicationStatus.APPLIED));

    JobApplicationResponse response =
        jobApplicationService.transitionStatus(OWNER_ID, APPLICATION_ID, request);

    assertThat(response.status()).isEqualTo(ApplicationStatus.APPLIED);
    assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
    verify(applicationStateValidator)
        .validateTransition(ApplicationStatus.DRAFT, ApplicationStatus.APPLIED);
    verify(applicationStatusHistoryRepository, times(1)).save(any(ApplicationStatusHistory.class));
    verify(careerAuditService)
        .record(
            OWNER_ID,
            OWNER_ID,
            CareerAuditAction.STATUS_CHANGED,
            "JobApplication",
            application.getId(),
            "status changed from DRAFT to APPLIED");
    verify(careerDomainEventPublisher, times(2)).publish(any());
  }

  @Test
  void shouldRejectInvalidTransitionViaStateValidator() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));
    StatusTransitionRequest request = new StatusTransitionRequest(ApplicationStatus.OFFER, null);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    org.mockito.Mockito.doThrow(
            new InvalidApplicationStatusTransitionException(
                ApplicationStatus.DRAFT, ApplicationStatus.OFFER))
        .when(applicationStateValidator)
        .validateTransition(ApplicationStatus.DRAFT, ApplicationStatus.OFFER);

    assertThatThrownBy(
            () -> jobApplicationService.transitionStatus(OWNER_ID, APPLICATION_ID, request))
        .isInstanceOf(InvalidApplicationStatusTransitionException.class);
    verify(applicationStatusHistoryRepository, never()).save(any());
    verify(careerDomainEventPublisher, never()).publish(any());
  }

  @Test
  void shouldArchiveApplicationAsSoftDelete() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));

    jobApplicationService.archive(OWNER_ID, APPLICATION_ID);

    assertThat(application.isArchived()).isTrue();
    assertThat(application.getArchivedAt()).isNotNull();
    verify(jobApplicationRepository, never()).delete(any(JobApplication.class));
    verify(careerAuditService)
        .record(
            OWNER_ID,
            OWNER_ID,
            CareerAuditAction.APPLICATION_ARCHIVED,
            "JobApplication",
            application.getId(),
            null);
  }

  @Test
  void shouldRejectMutationOfArchivedApplication() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));
    application.archive();
    StatusTransitionRequest request = new StatusTransitionRequest(ApplicationStatus.APPLIED, null);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));

    assertThatThrownBy(
            () -> jobApplicationService.transitionStatus(OWNER_ID, APPLICATION_ID, request))
        .isInstanceOf(ApplicationArchivedException.class);
  }

  private static JobApplicationResponse sampleResponse(ApplicationStatus status) {
    return new JobApplicationResponse(
        APPLICATION_ID,
        new CompanySummaryResponse(COMPANY_ID, "Acme Corp"),
        null,
        "Staff Engineer",
        null,
        null,
        status,
        null,
        null,
        null,
        LocalDate.of(2026, 8, 1),
        null,
        null,
        null,
        false,
        null,
        Instant.parse("2026-08-04T06:00:00Z"),
        Instant.parse("2026-08-04T06:00:00Z"),
        0L);
  }
}
