package com.acos.career.service;

import com.acos.career.dto.OfferRequest;
import com.acos.career.dto.OfferResponse;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Offer;
import com.acos.career.entity.OfferStatus;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.event.OfferAcceptedEvent;
import com.acos.career.event.OfferDeclinedEvent;
import com.acos.career.event.OfferReceivedEvent;
import com.acos.career.exception.ActiveOfferAlreadyExistsException;
import com.acos.career.exception.ApplicationArchivedException;
import com.acos.career.exception.JobApplicationNotFoundException;
import com.acos.career.exception.OfferNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.OfferRepository;
import com.acos.career.validator.CareerValidator;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link OfferService} implementation. */
@Service
@Transactional
public class OfferServiceImpl implements OfferService {

  private final OfferRepository offerRepository;
  private final JobApplicationRepository jobApplicationRepository;
  private final JobApplicationService jobApplicationService;
  private final CareerMapper careerMapper;
  private final CareerValidator careerValidator;
  private final CareerAuditService careerAuditService;
  private final CareerDomainEventPublisher careerDomainEventPublisher;

  /**
   * Creates the offer service.
   *
   * @param offerRepository offer repository
   * @param jobApplicationRepository application repository
   * @param jobApplicationService application service, used for optional status synchronization
   * @param careerMapper mapper
   * @param careerValidator validator
   * @param careerAuditService audit service
   * @param careerDomainEventPublisher domain event publisher
   */
  public OfferServiceImpl(
      OfferRepository offerRepository,
      JobApplicationRepository jobApplicationRepository,
      JobApplicationService jobApplicationService,
      CareerMapper careerMapper,
      CareerValidator careerValidator,
      CareerAuditService careerAuditService,
      CareerDomainEventPublisher careerDomainEventPublisher) {
    this.offerRepository =
        Objects.requireNonNull(offerRepository, "offerRepository must not be null");
    this.jobApplicationRepository =
        Objects.requireNonNull(
            jobApplicationRepository, "jobApplicationRepository must not be null");
    this.jobApplicationService =
        Objects.requireNonNull(jobApplicationService, "jobApplicationService must not be null");
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
  public OfferResponse create(UUID ownerId, UUID applicationId, OfferRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    JobApplication application = requireOwnedApplication(ownerId, applicationId);
    requireNotArchived(application);
    if (offerRepository.existsActiveByApplicationId(applicationId)) {
      throw new ActiveOfferAlreadyExistsException(applicationId);
    }
    validateRequest(request, Instant.now());

    Offer offer =
        new Offer(
            application,
            request.baseSalary(),
            careerValidator.normalizeCurrency(request.currency()));
    applyOptionalFields(offer, request);
    Offer saved = offerRepository.save(offer);

    careerAuditService.record(
        ownerId, ownerId, CareerAuditAction.OFFER_CREATED, "Offer", saved.getId(), null);
    careerDomainEventPublisher.publish(
        new OfferReceivedEvent(saved.getId(), applicationId, ownerId, Instant.now()));
    return careerMapper.toOfferResponse(saved);
  }

  @Override
  public OfferResponse update(
      UUID ownerId, UUID applicationId, UUID offerId, OfferRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(offerId, "offerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Offer offer = requireOwnedOffer(ownerId, applicationId, offerId);
    requireNotArchived(offer.getApplication());
    validateRequest(request, offer.getCreatedAt());

    OfferStatus previousStatus = offer.getOfferStatus();
    offer.setBaseSalary(request.baseSalary());
    offer.setCurrency(careerValidator.normalizeCurrency(request.currency()));
    offer.setOfferStatus(request.offerStatus());
    applyOptionalFields(offer, request);

    if (request.offerStatus() == previousStatus) {
      careerAuditService.record(
          ownerId, ownerId, CareerAuditAction.OFFER_UPDATED, "Offer", offer.getId(), null);
    } else {
      handleStatusChange(ownerId, applicationId, offer, request.offerStatus());
    }
    return careerMapper.toOfferResponse(offer);
  }

  @Override
  public void delete(UUID ownerId, UUID applicationId, UUID offerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(offerId, "offerId must not be null");
    Offer offer = requireOwnedOffer(ownerId, applicationId, offerId);
    offer.archive();
  }

  @Override
  @Transactional(readOnly = true)
  public OfferResponse get(UUID ownerId, UUID applicationId, UUID offerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    Objects.requireNonNull(offerId, "offerId must not be null");
    return careerMapper.toOfferResponse(requireOwnedOffer(ownerId, applicationId, offerId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OfferResponse> list(UUID ownerId, UUID applicationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(applicationId, "applicationId must not be null");
    requireOwnedApplication(ownerId, applicationId);
    return offerRepository.findByApplicationIdAndOwner(applicationId, ownerId).stream()
        .map(careerMapper::toOfferResponse)
        .toList();
  }

  private void handleStatusChange(
      UUID ownerId, UUID applicationId, Offer offer, OfferStatus newStatus) {
    Instant now = Instant.now();
    if (newStatus == OfferStatus.ACCEPTED) {
      careerAuditService.record(
          ownerId, ownerId, CareerAuditAction.OFFER_ACCEPTED, "Offer", offer.getId(), null);
      careerDomainEventPublisher.publish(
          new OfferAcceptedEvent(offer.getId(), applicationId, ownerId, now));
      jobApplicationService.trySyncStatus(
          ownerId, applicationId, ApplicationStatus.ACCEPTED, ownerId);
    } else if (newStatus == OfferStatus.DECLINED) {
      careerAuditService.record(
          ownerId, ownerId, CareerAuditAction.OFFER_DECLINED, "Offer", offer.getId(), null);
      careerDomainEventPublisher.publish(
          new OfferDeclinedEvent(offer.getId(), applicationId, ownerId, now));
      jobApplicationService.trySyncStatus(
          ownerId, applicationId, ApplicationStatus.DECLINED, ownerId);
    } else {
      careerAuditService.record(
          ownerId, ownerId, CareerAuditAction.OFFER_UPDATED, "Offer", offer.getId(), null);
    }
  }

  private void validateRequest(OfferRequest request, Instant offerCreatedAt) {
    careerValidator.validateNotesLength(request.notes());
    careerValidator.validateAmount("baseSalary", request.baseSalary());
    careerValidator.validateAmount("joiningBonus", request.joiningBonus());
    careerValidator.validateAmount("annualBonus", request.annualBonus());
    careerValidator.validateNoticePeriodDays(request.noticePeriodDays());
    careerValidator.validateJoiningDateAfterOfferCreation(request.joiningDate(), offerCreatedAt);
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

  private Offer requireOwnedOffer(UUID ownerId, UUID applicationId, UUID offerId) {
    return offerRepository
        .findByIdAndApplicationIdAndOwner(offerId, applicationId, ownerId)
        .orElseThrow(() -> new OfferNotFoundException(offerId));
  }

  private void applyOptionalFields(Offer offer, OfferRequest request) {
    offer.setJoiningBonus(request.joiningBonus());
    offer.setAnnualBonus(request.annualBonus());
    offer.setStockOptions(careerValidator.normalizeOptionalText(request.stockOptions()));
    offer.setLocation(careerValidator.normalizeOptionalText(request.location()));
    offer.setWorkMode(request.workMode());
    offer.setJoiningDate(request.joiningDate());
    offer.setNoticePeriodDays(request.noticePeriodDays());
    offer.setOfferExpiryDate(request.offerExpiryDate());
    offer.setNotes(careerValidator.normalizeOptionalText(request.notes()));
  }
}
