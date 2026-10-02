package com.acos.career.service;

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
import com.acos.common.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link CompanyService} implementation. */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final JobApplicationRepository jobApplicationRepository;
  private final CareerMapper careerMapper;
  private final CareerValidator careerValidator;

  /**
   * Creates the company service.
   *
   * @param companyRepository company repository
   * @param jobApplicationRepository application repository, used to guard deletion
   * @param careerMapper mapper
   * @param careerValidator validator
   */
  public CompanyServiceImpl(
      CompanyRepository companyRepository,
      JobApplicationRepository jobApplicationRepository,
      CareerMapper careerMapper,
      CareerValidator careerValidator) {
    this.companyRepository =
        Objects.requireNonNull(companyRepository, "companyRepository must not be null");
    this.jobApplicationRepository =
        Objects.requireNonNull(
            jobApplicationRepository, "jobApplicationRepository must not be null");
    this.careerMapper = Objects.requireNonNull(careerMapper, "careerMapper must not be null");
    this.careerValidator =
        Objects.requireNonNull(careerValidator, "careerValidator must not be null");
  }

  @Override
  public CompanyResponse create(UUID ownerId, CompanyRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    String name = request.name().trim();
    careerValidator.validateNotesLength(request.notes());
    if (companyRepository.existsByOwnerIdAndNameAndArchivedFalse(ownerId, name)) {
      throw new DuplicateCompanyNameException(name);
    }

    Company company = new Company(ownerId, name);
    applyMutableFields(company, request);
    Company saved = companyRepository.save(company);
    return careerMapper.toCompanyResponse(saved);
  }

  @Override
  public CompanyResponse update(UUID ownerId, UUID companyId, CompanyRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(companyId, "companyId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Company company = requireOwnedCompany(ownerId, companyId);
    String name = request.name().trim();
    careerValidator.validateNotesLength(request.notes());
    if (companyRepository.existsByOwnerIdAndNameAndArchivedFalseAndIdNot(
        ownerId, name, companyId)) {
      throw new DuplicateCompanyNameException(name);
    }

    company.setName(name);
    applyMutableFields(company, request);
    return careerMapper.toCompanyResponse(company);
  }

  @Override
  public void delete(UUID ownerId, UUID companyId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(companyId, "companyId must not be null");
    Company company = requireOwnedCompany(ownerId, companyId);
    if (jobApplicationRepository.existsByCompanyId(companyId)) {
      throw new BusinessException(
          ErrorCode.BUSINESS_RULE_VIOLATION,
          "Company cannot be deleted while job applications reference it");
    }
    company.archive();
  }

  @Override
  @Transactional(readOnly = true)
  public CompanyResponse get(UUID ownerId, UUID companyId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(companyId, "companyId must not be null");
    return careerMapper.toCompanyResponse(requireOwnedCompany(ownerId, companyId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CompanyResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return companyRepository.findByOwnerIdAndArchivedFalseOrderByNameAsc(ownerId).stream()
        .map(careerMapper::toCompanyResponse)
        .toList();
  }

  private Company requireOwnedCompany(UUID ownerId, UUID companyId) {
    return companyRepository
        .findByIdAndOwnerIdAndArchivedFalse(companyId, ownerId)
        .orElseThrow(() -> new CompanyNotFoundException(companyId));
  }

  private void applyMutableFields(Company company, CompanyRequest request) {
    company.setWebsite(careerValidator.normalizeOptionalText(request.website()));
    company.setIndustry(careerValidator.normalizeOptionalText(request.industry()));
    company.setLocation(careerValidator.normalizeOptionalText(request.location()));
    company.setNotes(careerValidator.normalizeOptionalText(request.notes()));
  }
}
