package com.acos.career.service;

import com.acos.career.dto.RecruiterRequest;
import com.acos.career.dto.RecruiterResponse;
import com.acos.career.entity.Company;
import com.acos.career.entity.Recruiter;
import com.acos.career.entity.RecruiterStatus;
import com.acos.career.exception.CompanyNotFoundException;
import com.acos.career.exception.DuplicateRecruiterEmailException;
import com.acos.career.exception.RecruiterNotFoundException;
import com.acos.career.mapper.CareerMapper;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.RecruiterRepository;
import com.acos.career.validator.CareerValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link RecruiterService} implementation. */
@Service
@Transactional
public class RecruiterServiceImpl implements RecruiterService {

  private final RecruiterRepository recruiterRepository;
  private final CompanyRepository companyRepository;
  private final CareerMapper careerMapper;
  private final CareerValidator careerValidator;

  /**
   * Creates the recruiter service.
   *
   * @param recruiterRepository recruiter repository
   * @param companyRepository company repository
   * @param careerMapper mapper
   * @param careerValidator validator
   */
  public RecruiterServiceImpl(
      RecruiterRepository recruiterRepository,
      CompanyRepository companyRepository,
      CareerMapper careerMapper,
      CareerValidator careerValidator) {
    this.recruiterRepository =
        Objects.requireNonNull(recruiterRepository, "recruiterRepository must not be null");
    this.companyRepository =
        Objects.requireNonNull(companyRepository, "companyRepository must not be null");
    this.careerMapper = Objects.requireNonNull(careerMapper, "careerMapper must not be null");
    this.careerValidator =
        Objects.requireNonNull(careerValidator, "careerValidator must not be null");
  }

  @Override
  public RecruiterResponse create(UUID ownerId, RecruiterRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    careerValidator.validateNotesLength(request.notes());
    String email = careerValidator.normalizeOptionalText(request.email());
    ensureUniqueEmail(ownerId, email, null);
    Recruiter recruiter = new Recruiter(ownerId, request.fullName().trim());
    applyMutableFields(ownerId, recruiter, request, email);
    Recruiter saved = recruiterRepository.save(recruiter);
    return careerMapper.toRecruiterResponse(saved);
  }

  @Override
  public RecruiterResponse update(UUID ownerId, UUID recruiterId, RecruiterRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(recruiterId, "recruiterId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Recruiter recruiter = requireOwnedRecruiter(ownerId, recruiterId);
    careerValidator.validateNotesLength(request.notes());
    String email = careerValidator.normalizeOptionalText(request.email());
    ensureUniqueEmail(ownerId, email, recruiterId);
    recruiter.setFullName(request.fullName().trim());
    applyMutableFields(ownerId, recruiter, request, email);
    return careerMapper.toRecruiterResponse(recruiter);
  }

  @Override
  public void delete(UUID ownerId, UUID recruiterId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(recruiterId, "recruiterId must not be null");
    Recruiter recruiter = requireOwnedRecruiter(ownerId, recruiterId);
    recruiter.archive();
  }

  @Override
  @Transactional(readOnly = true)
  public RecruiterResponse get(UUID ownerId, UUID recruiterId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(recruiterId, "recruiterId must not be null");
    return careerMapper.toRecruiterResponse(requireOwnedRecruiter(ownerId, recruiterId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<RecruiterResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return recruiterRepository.findByOwnerIdAndArchivedFalseOrderByFullNameAsc(ownerId).stream()
        .map(careerMapper::toRecruiterResponse)
        .toList();
  }

  private Recruiter requireOwnedRecruiter(UUID ownerId, UUID recruiterId) {
    return recruiterRepository
        .findByIdAndOwnerIdAndArchivedFalse(recruiterId, ownerId)
        .orElseThrow(() -> new RecruiterNotFoundException(recruiterId));
  }

  private Company resolveOwnedCompany(UUID ownerId, UUID companyId) {
    if (companyId == null) {
      return null;
    }
    return companyRepository
        .findByIdAndOwnerIdAndArchivedFalse(companyId, ownerId)
        .orElseThrow(() -> new CompanyNotFoundException(companyId));
  }

  private void ensureUniqueEmail(UUID ownerId, String email, UUID excludeRecruiterId) {
    if (email == null) {
      return;
    }
    boolean duplicate =
        excludeRecruiterId == null
            ? recruiterRepository.existsActiveByOwnerIdAndEmailIgnoreCase(ownerId, email)
            : recruiterRepository.existsActiveByOwnerIdAndEmailIgnoreCaseAndIdNot(
                ownerId, email, excludeRecruiterId);
    if (duplicate) {
      throw new DuplicateRecruiterEmailException(email);
    }
  }

  private void applyMutableFields(
      UUID ownerId, Recruiter recruiter, RecruiterRequest request, String email) {
    recruiter.setCompany(resolveOwnedCompany(ownerId, request.companyId()));
    recruiter.setEmail(email);
    recruiter.setPhone(careerValidator.normalizeOptionalText(request.phone()));
    recruiter.setLinkedInUrl(careerValidator.normalizeOptionalText(request.linkedInUrl()));
    recruiter.setLastContactDate(request.lastContactDate());
    recruiter.setNextFollowUpDate(request.nextFollowUpDate());
    recruiter.setStatus(request.status() == null ? RecruiterStatus.ACTIVE : request.status());
    recruiter.setNotes(careerValidator.normalizeOptionalText(request.notes()));
  }
}
