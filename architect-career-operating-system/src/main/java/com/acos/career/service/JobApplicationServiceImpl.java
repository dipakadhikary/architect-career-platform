package com.acos.career.service;

import com.acos.career.dto.ApplicationStatusHistoryResponse;
import com.acos.career.dto.ApplicationTimelineResponse;
import com.acos.career.dto.JobApplicationPageResponse;
import com.acos.career.dto.JobApplicationRequest;
import com.acos.career.dto.JobApplicationResponse;
import com.acos.career.dto.JobApplicationSearchCriteria;
import com.acos.career.dto.StatusTransitionRequest;
import com.acos.career.dto.TimelineStepResponse;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.ApplicationStatusHistory;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.Company;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Recruiter;
import com.acos.career.event.ApplicationRejectedEvent;
import com.acos.career.event.ApplicationStatusChangedEvent;
import com.acos.career.event.ApplicationSubmittedEvent;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.exception.ApplicationArchivedException;
import com.acos.career.exception.CompanyNotFoundException;
import com.acos.career.exception.JobApplicationNotFoundException;
import com.acos.career.exception.RecruiterNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.ApplicationStatusHistoryRepository;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.RecruiterRepository;
import com.acos.career.specification.JobApplicationSpecifications;
import com.acos.career.state.ApplicationStateMachine;
import com.acos.career.state.ApplicationStateValidator;
import com.acos.career.validator.CareerValidator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link JobApplicationService} implementation. */
@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

  private static final List<ApplicationStatus> MAIN_PIPELINE =
      List.of(
          ApplicationStatus.DRAFT,
          ApplicationStatus.APPLIED,
          ApplicationStatus.SCREENING,
          ApplicationStatus.TECHNICAL_INTERVIEW,
          ApplicationStatus.MANAGER_INTERVIEW,
          ApplicationStatus.HR_INTERVIEW,
          ApplicationStatus.OFFER,
          ApplicationStatus.ACCEPTED);

  private static final Set<ApplicationStatus> ALTERNATE_TERMINAL_STATUSES =
      Set.of(ApplicationStatus.DECLINED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN);

  private final JobApplicationRepository jobApplicationRepository;
  private final CompanyRepository companyRepository;
  private final RecruiterRepository recruiterRepository;
  private final ApplicationStatusHistoryRepository applicationStatusHistoryRepository;
  private final CareerMapper careerMapper;
  private final CareerValidator careerValidator;
  private final ApplicationStateValidator applicationStateValidator;
  private final CareerAuditService careerAuditService;
  private final CareerDomainEventPublisher careerDomainEventPublisher;

  /**
   * Creates the job application service.
   *
   * @param jobApplicationRepository application repository
   * @param companyRepository company repository
   * @param recruiterRepository recruiter repository
   * @param applicationStatusHistoryRepository status history repository
   * @param careerMapper mapper
   * @param careerValidator validator
   * @param applicationStateValidator status transition validator
   * @param careerAuditService audit service
   * @param careerDomainEventPublisher domain event publisher
   */
  public JobApplicationServiceImpl(
      JobApplicationRepository jobApplicationRepository,
      CompanyRepository companyRepository,
      RecruiterRepository recruiterRepository,
      ApplicationStatusHistoryRepository applicationStatusHistoryRepository,
      CareerMapper careerMapper,
      CareerValidator careerValidator,
      ApplicationStateValidator applicationStateValidator,
      CareerAuditService careerAuditService,
      CareerDomainEventPublisher careerDomainEventPublisher) {
    this.jobApplicationRepository =
        Objects.requireNonNull(
            jobApplicationRepository, "jobApplicationRepository must not be null");
    this.companyRepository =
        Objects.requireNonNull(companyRepository, "companyRepository must not be null");
    this.recruiterRepository =
        Objects.requireNonNull(recruiterRepository, "recruiterRepository must not be null");
    this.applicationStatusHistoryRepository =
        Objects.requireNonNull(
            applicationStatusHistoryRepository,
            "applicationStatusHistoryRepository must not be null");
    this.careerMapper = Objects.requireNonNull(careerMapper, "careerMapper must not be null");
    this.careerValidator =
        Objects.requireNonNull(careerValidator, "careerValidator must not be null");
    this.applicationStateValidator =
        Objects.requireNonNull(
            applicationStateValidator, "applicationStateValidator must not be null");
    this.careerAuditService =
        Objects.requireNonNull(careerAuditService, "careerAuditService must not be null");
    this.careerDomainEventPublisher =
        Objects.requireNonNull(
            careerDomainEventPublisher, "careerDomainEventPublisher must not be null");
  }

  @Override
  public JobApplicationResponse create(UUID ownerId, JobApplicationRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    validateRequest(request);
    Company company = requireOwnedCompany(ownerId, request.companyId());
    Recruiter recruiter = resolveOwnedRecruiter(ownerId, request.recruiterId());

    JobApplication application =
        new JobApplication(
            ownerId,
            company,
            careerValidator.normalizeRequiredText(request.title()),
            request.appliedOn());
    application.setRecruiter(recruiter);
    applyOptionalFields(application, request);

    JobApplication saved = jobApplicationRepository.save(application);
    ApplicationStatusHistory initialHistory =
        new ApplicationStatusHistory(saved, null, ApplicationStatus.DRAFT, ownerId);
    applicationStatusHistoryRepository.save(initialHistory);
    careerAuditService.record(
        ownerId,
        ownerId,
        CareerAuditAction.APPLICATION_CREATED,
        "JobApplication",
        saved.getId(),
        null);
    return careerMapper.toJobApplicationResponse(saved);
  }

  @Override
  public JobApplicationResponse update(
      UUID ownerId, UUID applicationId, JobApplicationRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    JobApplication application = requireOwnedApplicationWithDetails(ownerId, applicationId);
    requireNotArchived(application);
    validateRequest(request);

    application.setCompany(requireOwnedCompany(ownerId, request.companyId()));
    application.setRecruiter(resolveOwnedRecruiter(ownerId, request.recruiterId()));
    application.setTitle(careerValidator.normalizeRequiredText(request.title()));
    application.setAppliedOn(request.appliedOn());
    applyOptionalFields(application, request);
    careerAuditService.record(
        ownerId,
        ownerId,
        CareerAuditAction.APPLICATION_UPDATED,
        "JobApplication",
        application.getId(),
        null);
    return careerMapper.toJobApplicationResponse(application);
  }

  @Override
  public void archive(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    if (application.isArchived()) {
      return;
    }
    application.archive();
    careerAuditService.record(
        ownerId,
        ownerId,
        CareerAuditAction.APPLICATION_ARCHIVED,
        "JobApplication",
        application.getId(),
        null);
  }

  @Override
  @Transactional(readOnly = true)
  public JobApplicationResponse get(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    return careerMapper.toJobApplicationResponse(
        requireOwnedApplicationWithDetails(ownerId, applicationId));
  }

  @Override
  @Transactional(readOnly = true)
  public JobApplicationPageResponse list(UUID ownerId, Pageable pageable, boolean archived) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    careerValidator.validatePageable(pageable);
    Page<JobApplication> page =
        jobApplicationRepository.findByOwnerIdAndArchived(ownerId, archived, pageable);
    return careerMapper.toPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public JobApplicationPageResponse search(
      UUID ownerId, JobApplicationSearchCriteria criteria, Pageable pageable) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(criteria, "criteria must not be null");
    careerValidator.validatePageable(pageable);

    Specification<JobApplication> specification =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.notArchived(),
            JobApplicationSpecifications.companyId(criteria.companyId()),
            JobApplicationSpecifications.recruiterId(criteria.recruiterId()),
            JobApplicationSpecifications.status(criteria.status()),
            JobApplicationSpecifications.interviewRound(criteria.interviewRound()),
            JobApplicationSpecifications.appliedBetween(
                criteria.appliedFrom(), criteria.appliedTo()),
            JobApplicationSpecifications.salaryBetween(criteria.salaryMin(), criteria.salaryMax()),
            JobApplicationSpecifications.keyword(criteria.keyword()));

    Page<JobApplication> page = jobApplicationRepository.findAll(specification, pageable);
    return careerMapper.toPageResponse(page);
  }

  @Override
  public JobApplicationResponse transitionStatus(
      UUID ownerId, UUID applicationId, StatusTransitionRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    requireNotArchived(application);
    applicationStateValidator.validateTransition(application.getStatus(), request.newStatus());
    applyTransition(
        application,
        request.newStatus(),
        ownerId,
        careerValidator.normalizeOptionalText(request.comments()));
    return careerMapper.toJobApplicationResponse(application);
  }

  @Override
  public void trySyncStatus(
      UUID ownerId, UUID applicationId, ApplicationStatus targetStatus, UUID actorId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(targetStatus, "targetStatus must not be null");
    Objects.requireNonNull(actorId, "actorId must not be null");

    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    if (application.isArchived()) {
      return;
    }
    if (ApplicationStateMachine.isTransitionAllowed(application.getStatus(), targetStatus)) {
      applyTransition(application, targetStatus, actorId, null);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationStatusHistoryResponse> getHistory(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    requireOwnedApplication(ownerId, applicationId);
    return applicationStatusHistoryRepository
        .findByApplicationIdOrderByChangedAtAsc(applicationId)
        .stream()
        .map(careerMapper::toHistoryResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ApplicationTimelineResponse getTimeline(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    List<ApplicationStatusHistory> history =
        applicationStatusHistoryRepository.findByApplicationIdOrderByChangedAtAsc(applicationId);
    return buildTimeline(application.getStatus(), history);
  }

  private void applyTransition(
      JobApplication application, ApplicationStatus target, UUID actorId, String comments) {
    ApplicationStatus current = application.getStatus();
    application.setStatus(target);
    ApplicationStatusHistory history =
        new ApplicationStatusHistory(application, current, target, actorId);
    history.setComments(comments);
    applicationStatusHistoryRepository.save(history);
    careerAuditService.record(
        application.getOwnerId(),
        actorId,
        CareerAuditAction.STATUS_CHANGED,
        "JobApplication",
        application.getId(),
        "status changed from " + current + " to " + target);

    Instant now = Instant.now();
    careerDomainEventPublisher.publish(
        new ApplicationStatusChangedEvent(
            application.getId(), application.getOwnerId(), current, target, now));
    if (target == ApplicationStatus.APPLIED) {
      careerDomainEventPublisher.publish(
          new ApplicationSubmittedEvent(application.getId(), application.getOwnerId(), now));
    } else if (target == ApplicationStatus.REJECTED) {
      careerDomainEventPublisher.publish(
          new ApplicationRejectedEvent(application.getId(), application.getOwnerId(), now));
    }
  }

  private ApplicationTimelineResponse buildTimeline(
      ApplicationStatus current, List<ApplicationStatusHistory> history) {
    Map<ApplicationStatus, Instant> reachedAt = new LinkedHashMap<>();
    for (ApplicationStatusHistory entry : history) {
      reachedAt.put(entry.getNewStatus(), entry.getChangedAt());
    }

    List<TimelineStepResponse> steps = new ArrayList<>();
    for (ApplicationStatus milestone : MAIN_PIPELINE) {
      boolean reached = reachedAt.containsKey(milestone);
      boolean isCurrent = milestone == current;
      steps.add(
          new TimelineStepResponse(
              milestone, milestoneLabel(milestone), reached, isCurrent, reachedAt.get(milestone)));
    }
    if (ALTERNATE_TERMINAL_STATUSES.contains(current)) {
      steps.add(
          new TimelineStepResponse(
              current, milestoneLabel(current), true, true, reachedAt.get(current)));
    }
    return new ApplicationTimelineResponse(steps);
  }

  private static String milestoneLabel(ApplicationStatus status) {
    String[] words = status.name().split("_");
    StringBuilder builder = new StringBuilder();
    for (String word : words) {
      if (builder.length() > 0) {
        builder.append(' ');
      }
      builder.append(word.charAt(0)).append(word.substring(1).toLowerCase(Locale.ROOT));
    }
    return builder.toString();
  }

  private void validateRequest(JobApplicationRequest request) {
    careerValidator.validateNotesLength(request.notes());
    careerValidator.validateJobDescriptionLength(request.jobDescription());
    careerValidator.validateAmount("salaryExpectation", request.salaryExpectation());
    careerValidator.requireResumeVersion(request.resumeVersion());
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

  private JobApplication requireOwnedApplicationWithDetails(UUID ownerId, UUID applicationId) {
    return jobApplicationRepository
        .findWithDetailsByIdAndOwnerId(applicationId, ownerId)
        .orElseThrow(() -> new JobApplicationNotFoundException(applicationId));
  }

  private Company requireOwnedCompany(UUID ownerId, UUID companyId) {
    return companyRepository
        .findByIdAndOwnerIdAndArchivedFalse(companyId, ownerId)
        .orElseThrow(() -> new CompanyNotFoundException(companyId));
  }

  private Recruiter resolveOwnedRecruiter(UUID ownerId, UUID recruiterId) {
    if (recruiterId == null) {
      return null;
    }
    return recruiterRepository
        .findByIdAndOwnerIdAndArchivedFalse(recruiterId, ownerId)
        .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));
  }

  private void applyOptionalFields(JobApplication application, JobApplicationRequest request) {
    application.setJobDescription(careerValidator.normalizeOptionalText(request.jobDescription()));
    application.setSource(careerValidator.normalizeOptionalText(request.source()));
    application.setJobUrl(careerValidator.normalizeOptionalText(request.jobUrl()));
    application.setLocation(careerValidator.normalizeOptionalText(request.location()));
    application.setSalaryExpectation(request.salaryExpectation());
    application.setCurrency(careerValidator.normalizeCurrency(request.currency()));
    application.setResumeVersion(careerValidator.requireResumeVersion(request.resumeVersion()));
    application.setNotes(careerValidator.normalizeOptionalText(request.notes()));
  }
}
