package com.acos.career.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.career.dto.CompanyRequest;
import com.acos.career.dto.CompanyResponse;
import com.acos.career.entity.Company;
import com.acos.career.exception.CompanyNotFoundException;
import com.acos.career.exception.DuplicateCompanyNameException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.validator.CareerValidator;
import com.acos.common.exception.BusinessException;
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

/** Unit tests for {@link CompanyServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID COMPANY_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private CompanyRepository companyRepository;
  @Mock private JobApplicationRepository jobApplicationRepository;
  @Mock private CareerMapper careerMapper;
  @Mock private CareerValidator careerValidator;

  private CompanyServiceImpl companyService;

  @BeforeEach
  void setUp() {
    companyService =
        new CompanyServiceImpl(
            companyRepository, jobApplicationRepository, careerMapper, careerValidator);
  }

  @Test
  void shouldCreateCompany() {
    CompanyRequest request =
        new CompanyRequest(" Acme Corp ", " https://acme.example.com ", "Technology", null, null);
    CompanyResponse expected =
        new CompanyResponse(
            COMPANY_ID,
            "Acme Corp",
            "https://acme.example.com",
            "Technology",
            null,
            null,
            false,
            null,
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"),
            0L);

    when(companyRepository.existsByOwnerIdAndNameAndArchivedFalse(OWNER_ID, "Acme Corp"))
        .thenReturn(false);
    when(careerValidator.normalizeOptionalText(" https://acme.example.com "))
        .thenReturn("https://acme.example.com");
    when(careerValidator.normalizeOptionalText("Technology")).thenReturn("Technology");
    when(careerValidator.normalizeOptionalText(null)).thenReturn(null);
    when(companyRepository.save(any(Company.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(careerMapper.toCompanyResponse(any(Company.class))).thenReturn(expected);

    CompanyResponse response = companyService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
    verify(companyRepository).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Acme Corp");
    assertThat(captor.getValue().getWebsite()).isEqualTo("https://acme.example.com");
  }

  @Test
  void shouldRejectDuplicateCompanyName() {
    CompanyRequest request = new CompanyRequest("Acme Corp", null, null, null, null);
    when(companyRepository.existsByOwnerIdAndNameAndArchivedFalse(OWNER_ID, "Acme Corp"))
        .thenReturn(true);

    assertThatThrownBy(() -> companyService.create(OWNER_ID, request))
        .isInstanceOf(DuplicateCompanyNameException.class)
        .hasMessageContaining("Acme Corp");
    verify(companyRepository, never()).save(any());
  }

  @Test
  void shouldSoftArchiveWhenNotReferenced() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    ReflectionTestUtils.setField(company, "id", COMPANY_ID);

    when(companyRepository.findByIdAndOwnerIdAndArchivedFalse(COMPANY_ID, OWNER_ID))
        .thenReturn(Optional.of(company));
    when(jobApplicationRepository.existsByCompanyId(COMPANY_ID)).thenReturn(false);

    companyService.delete(OWNER_ID, COMPANY_ID);

    assertThat(company.isArchived()).isTrue();
    assertThat(company.getArchivedAt()).isNotNull();
    verify(companyRepository, never()).delete(any(Company.class));
  }

  @Test
  void shouldRejectDeleteWhenReferencedByApplications() {
    Company company = new Company(OWNER_ID, "Acme Corp");
    ReflectionTestUtils.setField(company, "id", COMPANY_ID);

    when(companyRepository.findByIdAndOwnerIdAndArchivedFalse(COMPANY_ID, OWNER_ID))
        .thenReturn(Optional.of(company));
    when(jobApplicationRepository.existsByCompanyId(COMPANY_ID)).thenReturn(true);

    assertThatThrownBy(() -> companyService.delete(OWNER_ID, COMPANY_ID))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("cannot be deleted");
    assertThat(company.isArchived()).isFalse();
  }

  @Test
  void shouldFailWhenCompanyMissing() {
    when(companyRepository.findByIdAndOwnerIdAndArchivedFalse(COMPANY_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> companyService.get(OWNER_ID, COMPANY_ID))
        .isInstanceOf(CompanyNotFoundException.class)
        .hasMessageContaining(COMPANY_ID.toString());
  }
}
