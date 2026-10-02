package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.career.dto.RecruiterRequest;
import com.acos.career.dto.RecruiterResponse;
import com.acos.career.entity.Recruiter;
import com.acos.career.entity.RecruiterStatus;
import com.acos.career.exception.DuplicateRecruiterEmailException;
import com.acos.career.exception.RecruiterNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.RecruiterRepository;
import com.acos.career.validator.CareerValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link RecruiterServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class RecruiterServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID RECRUITER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final Instant CREATED_AT = Instant.parse("2026-08-04T06:00:00Z");

  @Mock private RecruiterRepository recruiterRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private CareerMapper careerMapper;
  @Mock private CareerValidator careerValidator;

  private RecruiterServiceImpl recruiterService;

  @BeforeEach
  void setUp() {
    recruiterService =
        new RecruiterServiceImpl(
            recruiterRepository, companyRepository, careerMapper, careerValidator);
  }

  @Test
  void shouldCreateRecruiter() {
    RecruiterRequest request =
        new RecruiterRequest(
            null,
            " Jamie Recruiter ",
            "jamie@agency.example.com",
            null,
            null,
            null,
            null,
            null,
            null);
    RecruiterResponse expected =
        new RecruiterResponse(
            RECRUITER_ID,
            null,
            "Jamie Recruiter",
            "jamie@agency.example.com",
            null,
            null,
            null,
            null,
            RecruiterStatus.ACTIVE,
            null,
            false,
            null,
            CREATED_AT,
            CREATED_AT,
            0L);

    when(careerValidator.normalizeOptionalText("jamie@agency.example.com"))
        .thenReturn("jamie@agency.example.com");
    when(careerValidator.normalizeOptionalText(null)).thenReturn(null);
    when(recruiterRepository.existsActiveByOwnerIdAndEmailIgnoreCase(
            OWNER_ID, "jamie@agency.example.com"))
        .thenReturn(false);
    when(recruiterRepository.save(any(Recruiter.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(careerMapper.toRecruiterResponse(any(Recruiter.class))).thenReturn(expected);

    RecruiterResponse response = recruiterService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<Recruiter> captor = ArgumentCaptor.forClass(Recruiter.class);
    verify(recruiterRepository).save(captor.capture());
    assertThat(captor.getValue().getFullName()).isEqualTo("Jamie Recruiter");
    assertThat(captor.getValue().getEmail()).isEqualTo("jamie@agency.example.com");
    assertThat(captor.getValue().getStatus()).isEqualTo(RecruiterStatus.ACTIVE);
  }

  @Test
  void shouldRejectDuplicateEmail() {
    RecruiterRequest request =
        new RecruiterRequest(
            null,
            "Jamie Recruiter",
            "jamie@agency.example.com",
            null,
            null,
            null,
            null,
            null,
            null);

    when(careerValidator.normalizeOptionalText("jamie@agency.example.com"))
        .thenReturn("jamie@agency.example.com");
    when(recruiterRepository.existsActiveByOwnerIdAndEmailIgnoreCase(
            OWNER_ID, "jamie@agency.example.com"))
        .thenReturn(true);

    assertThatThrownBy(() -> recruiterService.create(OWNER_ID, request))
        .isInstanceOf(DuplicateRecruiterEmailException.class)
        .hasMessageContaining("jamie@agency.example.com");
    verify(recruiterRepository, never()).save(any());
  }

  @Test
  void shouldSoftArchiveOnDelete() {
    Recruiter recruiter = new Recruiter(OWNER_ID, "Jamie Recruiter");
    ReflectionTestUtils.setField(recruiter, "id", RECRUITER_ID);

    when(recruiterRepository.findByIdAndOwnerIdAndArchivedFalse(RECRUITER_ID, OWNER_ID))
        .thenReturn(Optional.of(recruiter));

    recruiterService.delete(OWNER_ID, RECRUITER_ID);

    assertThat(recruiter.isArchived()).isTrue();
    assertThat(recruiter.getArchivedAt()).isNotNull();
    verify(recruiterRepository, never()).delete(any(Recruiter.class));
  }

  @Test
  void shouldFailWhenRecruiterMissing() {
    when(recruiterRepository.findByIdAndOwnerIdAndArchivedFalse(RECRUITER_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> recruiterService.get(OWNER_ID, RECRUITER_ID))
        .isInstanceOf(RecruiterNotFoundException.class)
        .hasMessageContaining(RECRUITER_ID.toString());
  }
}
