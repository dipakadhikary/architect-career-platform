package com.acos.career.service;

import com.acos.career.dto.InterviewRequest;
import com.acos.career.dto.InterviewResponse;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.Interview;
import com.acos.career.entity.InterviewStatus;
import com.acos.career.entity.JobApplication;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.event.InterviewCompletedEvent;
import com.acos.career.event.InterviewScheduledEvent;
import com.acos.career.exception.ApplicationArchivedException;
import com.acos.career.exception.InterviewNotFoundException;
import com.acos.career.exception.JobApplicationNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.InterviewRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.validator.CareerValidator;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link InterviewService} implementation. */
@Service
@Transactional
public class InterviewServiceImpl implements InterviewService {

  private final InterviewRepository interviewRepository;
  private final JobApplicationRepository jobApplicationRepository;
  private final CareerMapper careerMapper;
  private final CareerValidator careerValidator;
  private final CareerAuditService careerAuditService;
  private final CareerDomainEventPublisher careerDomainEventPublisher;

  /**
   * Creates the interview service.
   *
   * @param interviewRepository interview repository
   * @param jobApplicationRepository application repository
   * @param careerMapper mapper
   * @param careerValidator validator
   * @param careerAuditService audit service
   * @param careerDomainEventPublisher domain event publisher
   */
  public InterviewServiceImpl(
      InterviewRepository interviewRepository,
      JobApplicationRepository jobApplicationRepository,
      CareerMapper careerMapper,
      CareerValidator careerValidator,
      CareerAuditService careerAuditService,
      CareerDomainEventPublisher careerDomainEventPublisher) {
    this.interviewRepository =
        Objects.requireNonNull(interviewRepository, "interviewRepository must not be null");
    this.jobApplicationRepository =
        Objects.requireNonNull(
            jobApplicationRepository, "jobApplicationRepository must not be null");
    this.careerMapper = Objects.requireNonNull(careerMapper, "careerMapper must not be null");
    this.careerValidator =
        Objects.requireNonNull(careerValidator, "careerValidator must not be null");
    this.careerAuditService =
        Objects.requireNonNull(careerAuditService, "careerAuditService must not be null");
    this.careerDomainEventPublisher =
        Objects.requireNonNull(
            careerDomainEventPublisher, "careerDomainEventPublisher must not be null");
  }

  @Override
  public InterviewResponse create(UUID ownerId, UUID applicationId, InterviewRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    requireNotArchived(application);
    careerValidator.validateInterviewDateNotInPast(request.interviewDate(), Instant.now());
    validateRequest(request);

    Interview interview =
        new Interview(application, request.interviewRound(), request.interviewDate());
    applyOptionalFields(interview, request);
    Interview saved = interviewRepository.save(interview);

    careerAuditService.record(
        ownerId, ownerId, CareerAuditAction.INTERVIEW_SCHEDULED, "Interview", saved.getId(), null);
    careerDomainEventPublisher.publish(
        new InterviewScheduledEvent(saved.getId(), applicationId, ownerId, Instant.now()));
    return careerMapper.toInterviewResponse(saved);
  }

  @Override
  public InterviewResponse update(
      UUID ownerId, UUID applicationId, UUID interviewId, InterviewRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(interviewId, "interviewId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Interview interview = requireOwnedInterview(ownerId, applicationId, interviewId);
    requireNotArchived(interview.getApplication());
    validateRequest(request);

    InterviewStatus previousStatus = interview.getStatus();
    interview.setInterviewRound(request.interviewRound());
    interview.setInterviewDate(request.interviewDate());
    interview.setStatus(request.status());
    applyOptionalFields(interview, request);

    boolean justCompleted =
        request.status() == InterviewStatus.COMPLETED
            && previousStatus != InterviewStatus.COMPLETED;
    if (justCompleted) {
      careerAuditService.record(
          ownerId,
          ownerId,
          CareerAuditAction.INTERVIEW_COMPLETED,
          "Interview",
          interview.getId(),
          null);
      careerDomainEventPublisher.publish(
          new InterviewCompletedEvent(interview.getId(), applicationId, ownerId, Instant.now()));
    } else {
      careerAuditService.record(
          ownerId,
          ownerId,
          CareerAuditAction.INTERVIEW_UPDATED,
          "Interview",
          interview.getId(),
          null);
    }
    return careerMapper.toInterviewResponse(interview);
  }

  @Override
  public void delete(UUID ownerId, UUID applicationId, UUID interviewId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(interviewId, "interviewId must not be null");
    Interview interview = requireOwnedInterview(ownerId, applicationId, interviewId);
    interview.archive();
  }

  @Override
  @Transactional(readOnly = true)
  public InterviewResponse get(UUID ownerId, UUID applicationId, UUID interviewId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(interviewId, "interviewId must not be null");
    return careerMapper.toInterviewResponse(
        requireOwnedInterview(ownerId, applicationId, interviewId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<InterviewResponse> list(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    requireOwnedApplication(ownerId, applicationId);
    return interviewRepository.findByApplicationIdAndOwner(applicationId, ownerId).stream()
        .map(careerMapper::toInterviewResponse)
        .toList();
  }

  private void validateRequest(InterviewRequest request) {
    careerValidator.validateNotesLength(request.notes());
    careerValidator.validateDurationMinutes(request.durationMinutes());
    careerValidator.validateRating("rating", request.rating());
    careerValidator.validateRating("confidenceRating", request.confidenceRating());
  }

  private void requireNotArchived(JobApplication application) {
    if (application.isArchived()) {
      throw new ApplicationArchivedException(application.getId());
    }
  }

  private JobApplication requireOwnedApplication(UUID ownerId, UUID applicationId) {
    return jobApplicationRepository
        .findByIdAndOwnerId(applicationId, ownerId)
        .orElseThrow(() -> new JobApplicationNotFoundException(applicationId));
  }

  private Interview requireOwnedInterview(UUID ownerId, UUID applicationId, UUID interviewId) {
    return interviewRepository
        .findByIdAndApplicationIdAndOwner(interviewId, applicationId, ownerId)
        .orElseThrow(() -> new InterviewNotFoundException(interviewId));
  }

  private void applyOptionalFields(Interview interview, InterviewRequest request) {
    interview.setInterviewer(careerValidator.normalizeOptionalText(request.interviewer()));
    interview.setDurationMinutes(request.durationMinutes());
    interview.setRating(request.rating());
    interview.setFeedback(careerValidator.normalizeOptionalText(request.feedback()));
    interview.setQuestionsAsked(careerValidator.normalizeOptionalText(request.questionsAsked()));
    interview.setStrengths(careerValidator.normalizeOptionalText(request.strengths()));
    interview.setWeaknesses(careerValidator.normalizeOptionalText(request.weaknesses()));
    interview.setImprovementAreas(
        careerValidator.normalizeOptionalText(request.improvementAreas()));
    interview.setCandidateNotes(careerValidator.normalizeOptionalText(request.candidateNotes()));
    interview.setConfidenceRating(request.confidenceRating());
    interview.setInterviewReminderDate(request.interviewReminderDate());
    interview.setLocationOrLink(careerValidator.normalizeOptionalText(request.locationOrLink()));
    interview.setNotes(careerValidator.normalizeOptionalText(request.notes()));
  }
}
