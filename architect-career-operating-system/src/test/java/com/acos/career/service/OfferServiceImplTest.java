package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.career.dto.OfferRequest;
import com.acos.career.dto.OfferResponse;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.CareerAuditAction;
import com.acos.career.entity.Company;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Offer;
import com.acos.career.entity.OfferStatus;
import com.acos.career.event.CareerDomainEventPublisher;
import com.acos.career.event.OfferAcceptedEvent;
import com.acos.career.event.OfferReceivedEvent;
import com.acos.career.exception.ActiveOfferAlreadyExistsException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.OfferRepository;
import com.acos.career.validator.CareerValidator;
import java.math.BigDecimal;
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

/** Unit tests for {@link OfferServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID APPLICATION_ID =
      UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final UUID OFFER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
  private static final Instant CREATED_AT = Instant.parse("2026-08-04T06:00:00Z");

  @Mock private OfferRepository offerRepository;
  @Mock private JobApplicationRepository jobApplicationRepository;
  @Mock private JobApplicationService jobApplicationService;
  @Mock private CareerMapper careerMapper;
  @Mock private CareerValidator careerValidator;
  @Mock private CareerAuditService careerAuditService;
  @Mock private CareerDomainEventPublisher careerDomainEventPublisher;

  private OfferServiceImpl offerService;

  @BeforeEach
  void setUp() {
    offerService =
        new OfferServiceImpl(
            offerRepository,
            jobApplicationRepository,
            jobApplicationService,
            careerMapper,
            careerValidator,
            careerAuditService,
            careerDomainEventPublisher);
  }

  @Test
  void shouldCreatePendingOfferAndPublishReceivedEvent() {
    JobApplication application = activeApplication();
    OfferRequest request = pendingRequest();
    OfferResponse expected = sampleResponse(OfferStatus.PENDING);

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    when(offerRepository.existsActiveByApplicationId(APPLICATION_ID)).thenReturn(false);
    when(careerValidator.normalizeCurrency("USD")).thenReturn("USD");
    when(offerRepository.save(any(Offer.class)))
        .thenAnswer(
            invocation -> {
              Offer offer = invocation.getArgument(0);
              ReflectionTestUtils.setField(offer, "id", OFFER_ID);
              return offer;
            });
    when(careerMapper.toOfferResponse(any(Offer.class))).thenReturn(expected);

    OfferResponse response = offerService.create(OWNER_ID, APPLICATION_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<Offer> offerCaptor = ArgumentCaptor.forClass(Offer.class);
    verify(offerRepository).save(offerCaptor.capture());
    assertThat(offerCaptor.getValue().getOfferStatus()).isEqualTo(OfferStatus.PENDING);
    assertThat(offerCaptor.getValue().getBaseSalary()).isEqualByComparingTo("230000.00");
    verify(careerAuditService)
        .record(OWNER_ID, OWNER_ID, CareerAuditAction.OFFER_CREATED, "Offer", OFFER_ID, null);
    ArgumentCaptor<OfferReceivedEvent> eventCaptor =
        ArgumentCaptor.forClass(OfferReceivedEvent.class);
    verify(careerDomainEventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().offerId()).isEqualTo(OFFER_ID);
    assertThat(eventCaptor.getValue().applicationId()).isEqualTo(APPLICATION_ID);
  }

  @Test
  void shouldRejectWhenActiveOfferAlreadyExists() {
    JobApplication application = activeApplication();
    OfferRequest request = pendingRequest();

    when(jobApplicationRepository.findByIdAndOwnerId(APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(application));
    when(offerRepository.existsActiveByApplicationId(APPLICATION_ID)).thenReturn(true);

    assertThatThrownBy(() -> offerService.create(OWNER_ID, APPLICATION_ID, request))
        .isInstanceOf(ActiveOfferAlreadyExistsException.class)
        .hasMessageContaining(APPLICATION_ID.toString());
    verify(offerRepository, never()).save(any());
    verify(careerDomainEventPublisher, never()).publish(any());
  }

  @Test
  void shouldPublishAcceptedEventAndSyncStatus() {
    JobApplication application = activeApplication();
    Offer offer = new Offer(application, new BigDecimal("230000.00"), "USD");
    ReflectionTestUtils.setField(offer, "id", OFFER_ID);
    ReflectionTestUtils.setField(offer, "createdAt", CREATED_AT);
    OfferRequest request =
        new OfferRequest(
            new BigDecimal("230000.00"),
            "USD",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            OfferStatus.ACCEPTED,
            null,
            null);
    OfferResponse expected = sampleResponse(OfferStatus.ACCEPTED);

    when(offerRepository.findByIdAndApplicationIdAndOwner(OFFER_ID, APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(offer));
    when(careerValidator.normalizeCurrency("USD")).thenReturn("USD");
    when(careerMapper.toOfferResponse(offer)).thenReturn(expected);

    OfferResponse response = offerService.update(OWNER_ID, APPLICATION_ID, OFFER_ID, request);

    assertThat(response.offerStatus()).isEqualTo(OfferStatus.ACCEPTED);
    assertThat(offer.getOfferStatus()).isEqualTo(OfferStatus.ACCEPTED);
    verify(careerAuditService)
        .record(OWNER_ID, OWNER_ID, CareerAuditAction.OFFER_ACCEPTED, "Offer", OFFER_ID, null);
    ArgumentCaptor<OfferAcceptedEvent> eventCaptor =
        ArgumentCaptor.forClass(OfferAcceptedEvent.class);
    verify(careerDomainEventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().offerId()).isEqualTo(OFFER_ID);
    verify(jobApplicationService)
        .trySyncStatus(OWNER_ID, APPLICATION_ID, ApplicationStatus.ACCEPTED, OWNER_ID);
  }

  @Test
  void shouldSoftArchiveOnDelete() {
    JobApplication application = activeApplication();
    Offer offer = new Offer(application, new BigDecimal("230000.00"), "USD");
    ReflectionTestUtils.setField(offer, "id", OFFER_ID);

    when(offerRepository.findByIdAndApplicationIdAndOwner(OFFER_ID, APPLICATION_ID, OWNER_ID))
        .thenReturn(Optional.of(offer));

    offerService.delete(OWNER_ID, APPLICATION_ID, OFFER_ID);

    assertThat(offer.isArchived()).isTrue();
    assertThat(offer.getArchivedAt()).isNotNull();
    verify(offerRepository, never()).delete(any(Offer.class));
  }

  private static JobApplication activeApplication() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    JobApplication application =
        new JobApplication(OWNER_ID, company, "Staff Engineer", LocalDate.of(2026, 8, 1));
    ReflectionTestUtils.setField(application, "id", APPLICATION_ID);
    return application;
  }

  private static OfferRequest pendingRequest() {
    return new OfferRequest(
        new BigDecimal("230000.00"),
        "USD",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        OfferStatus.PENDING,
        null,
        null);
  }

  private static OfferResponse sampleResponse(OfferStatus status) {
    return new OfferResponse(
        OFFER_ID,
        APPLICATION_ID,
        new BigDecimal("230000.00"),
        "USD",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        status,
        null,
        null,
        false,
        null,
        CREATED_AT,
        CREATED_AT,
        0L);
  }
}
