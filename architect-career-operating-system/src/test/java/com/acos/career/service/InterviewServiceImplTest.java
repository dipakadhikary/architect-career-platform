package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.career.dto.InterviewRequest;
import com.acos.career.dto.InterviewResponse;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.Company;
import com.acos.career.entity.Interview;
import com.acos.career.entity.InterviewRound;
import com.acos.career.entity.InterviewStatus;
import com.acos.career.entity.JobApplication;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.event.InterviewCompletedEvent;
import com.acos.career.event.InterviewScheduledEvent;
import com.acos.career.exception.ApplicationArchivedException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.InterviewRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.validator.CareerValidator;
import com.acos.common.exception.ValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link InterviewServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID APPLICATION_ID =
      UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final UUID INTERVIEW_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  private static final Instant INTERVIEW_DATE = Instant.parse("2026-08-20T15:00:00Z");
  private static final Instant CREATED_AT = Instant.parse("2026-08-04T06:00:00Z");

  @Mock private InterviewRepository interviewRepository;
  @Mock private JobApplicationRepository jobApplicationRepository;
  @Mock private CareerMapper careerMapper;
  @Mock private CareerValidator careerValidator;
  @Mock private CareerAuditService careerAuditService;
  @Mock private CareerDomainEventPublisher careerDomainEventPublisher;

  private InterviewServiceImpl interviewService;

  @BeforeEach
  void setUp() {
    interviewService =
        new InterviewServiceImpl(
            interviewRepository,
            jobApplicationRepository,
            careerMapper,
            careerValidator,
            careerAuditService,
            careerDomainEventPublisher);
  }

  @Test
  void shouldCreateInterviewAndPublishScheduledEvent() {
    JobApplication application = activeApplication();
    InterviewRequest request = scheduledRequest(INTERVIEW_DATE);
    InterviewResponse expected = sampleResponse(InterviewStatus.SCHEDULED);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    when(interviewRepository.save(any(Interview.class)))
        .thenAnswer(
            invocation -> {
              Interview interview = invocation.getArgument(0);
              ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);
              return interview;
            });
    when(careerMapper.toInterviewResponse(any(Interview.class))).thenReturn(expected);

    InterviewResponse response = interviewService.create(OWNER_ID, APPLICATION_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<Interview> interviewCaptor = ArgumentCaptor.forClass(Interview.class);
    verify(interviewRepository).save(interviewCaptor.capture());
    assertThat(interviewCaptor.getValue().getInterviewRound()).isEqualTo(InterviewRound.TECHNICAL);
    assertThat(interviewCaptor.getValue().getStatus()).isEqualTo(InterviewStatus.SCHEDULED);
    verify(careerAuditService)
        .record(
            OWNER_ID,
            OWNER_ID,
            CareerAuditAction.INTERVIEW_SCHEDULED,
            "Interview",
            INTERVIEW_ID,
            null);
    ArgumentCaptor<InterviewScheduledEvent> eventCaptor =
        ArgumentCaptor.forClass(InterviewScheduledEvent.class);
    verify(careerDomainEventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().interviewId()).isEqualTo(INTERVIEW_ID);
    assertThat(eventCaptor.getValue().applicationId()).isEqualTo(APPLICATION_ID);
    assertThat(eventCaptor.getValue().ownerId()).isEqualTo(OWNER_ID);
  }

  @Test
  void shouldRejectPastInterviewDate() {
    JobApplication application = activeApplication();
    InterviewRequest request = scheduledRequest(Instant.parse("2020-01-01T00:00:00Z"));

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    doThrow(new ValidationException("interviewDate must not be in the past when scheduling"))
        .when(careerValidator)
        .validateInterviewDateNotInPast(any(Instant.class), any(Instant.class));

    assertThatThrownBy(() -> interviewService.create(OWNER_ID, APPLICATION_ID, request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("interviewDate");
    verify(interviewRepository, never()).save(any());
    verify(careerDomainEventPublisher, never()).publish(any());
  }

  @Test
  void shouldRejectCreateWhenApplicationArchived() {
    JobApplication application = activeApplication();
    application.archive();
    InterviewRequest request = scheduledRequest(INTERVIEW_DATE);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));

    assertThatThrownBy(() -> interviewService.create(OWNER_ID, APPLICATION_ID, request))
        .isInstanceOf(ApplicationArchivedException.class);
    verify(interviewRepository, never()).save(any());
  }

  @Test
  void shouldPublishCompletedEventWhenUpdatedToCompleted() {
    JobApplication application = activeApplication();
    Interview interview = new Interview(application, InterviewRound.TECHNICAL, INTERVIEW_DATE);
    ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);
    InterviewRequest request =
        new InterviewRequest(
            InterviewRound.TECHNICAL,
            null,
            INTERVIEW_DATE,
            60,
            InterviewStatus.COMPLETED,
            4,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    InterviewResponse expected = sampleResponse(InterviewStatus.COMPLETED);

    when(interviewRepository.findByIdAndApplicationIdAndOwner(
            INTERVIEW_ID, APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(interview));
    when(careerMapper.toInterviewResponse(interview)).thenReturn(expected);

    InterviewResponse response =
        interviewService.update(OWNER_ID, APPLICATION_ID, INTERVIEW_ID, request);

    assertThat(response.status()).isEqualTo(InterviewStatus.COMPLETED);
    assertThat(interview.getStatus()).isEqualTo(InterviewStatus.COMPLETED);
    verify(careerAuditService)
        .record(
            OWNER_ID,
            OWNER_ID,
            CareerAuditAction.INTERVIEW_COMPLETED,
            "Interview",
            INTERVIEW_ID,
            null);
    ArgumentCaptor<InterviewCompletedEvent> eventCaptor =
        ArgumentCaptor.forClass(InterviewCompletedEvent.class);
    verify(careerDomainEventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().interviewId()).isEqualTo(INTERVIEW_ID);
  }

  @Test
  void shouldGetInterview() {
    JobApplication application = activeApplication();
    Interview interview = new Interview(application, InterviewRound.TECHNICAL, INTERVIEW_DATE);
    ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);
    InterviewResponse expected = sampleResponse(InterviewStatus.SCHEDULED);

    when(interviewRepository.findByIdAndApplicationIdAndOwner(
            INTERVIEW_ID, APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(interview));
    when(careerMapper.toInterviewResponse(interview)).thenReturn(expected);

    assertThat(interviewService.get(OWNER_ID, APPLICATION_ID, INTERVIEW_ID)).isEqualTo(expected);
  }

  @Test
  void shouldListInterviews() {
    JobApplication application = activeApplication();
    Interview interview = new Interview(application, InterviewRound.TECHNICAL, INTERVIEW_DATE);
    ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);
    InterviewResponse expected = sampleResponse(InterviewStatus.SCHEDULED);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    when(interviewRepository.findByApplicationIdAndOwner(APPLICATION_ID, OWNER_ID))
        .thenReturn(List.of(interview));
    when(careerMapper.toInterviewResponse(interview)).thenReturn(expected);

    assertThat(interviewService.list(OWNER_ID, APPLICATION_ID)).containsExactly(expected);
  }

  @Test
  void shouldSoftArchiveOnDelete() {
    JobApplication application = activeApplication();
    Interview interview = new Interview(application, InterviewRound.TECHNICAL, INTERVIEW_DATE);
    ReflectionTestUtils.setField(interview, "id", INTERVIEW_ID);

    when(interviewRepository.findByIdAndApplicationIdAndOwner(
            INTERVIEW_ID, APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(interview));

    interviewService.delete(OWNER_ID, APPLICATION_ID, INTERVIEW_ID);

    assertThat(interview.isArchived()).isTrue();
    assertThat(interview.getArchivedAt()).isNotNull();
    verify(interviewRepository, never()).delete(any(Interview.class));
  }

  private static JobApplication activeApplication() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));
    ReflectionTestUtils.setField(application, "id", APPLICATION_ID);
    return application;
  }

  private static InterviewRequest scheduledRequest(Instant interviewDate) {
    return new InterviewRequest(
        InterviewRound.TECHNICAL,
        null,
        interviewDate,
        null,
        InterviewStatus.SCHEDULED,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  private static InterviewResponse sampleResponse(InterviewStatus status) {
    return new InterviewResponse(
        INTERVIEW_ID,
        APPLICATION_ID,
        InterviewRound.TECHNICAL,
        null,
        INTERVIEW_DATE,
        null,
        status,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        CREATED_AT,
        CREATED_AT,
        0L);
  }
}
