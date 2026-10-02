package com.acos.portfolio.service;

import com.acos.portfolio.dto.CertificationRequest;
import com.acos.portfolio.dto.CertificationResponse;
import com.acos.portfolio.entity.Certification;
import com.acos.portfolio.exception.CertificationNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.CertificationRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link CertificationService} implementation. */
@Service
@Transactional
public class CertificationServiceImpl implements CertificationService {

  private final CertificationRepository certificationRepository;
  private final PortfolioMapper portfolioMapper;
  private final PortfolioValidator portfolioValidator;

  /**
   * Creates the certification service.
   *
   * @param certificationRepository certification repository
   * @param portfolioMapper mapper
   * @param portfolioValidator validator
   */
  public CertificationServiceImpl(
      CertificationRepository certificationRepository,
      PortfolioMapper portfolioMapper,
      PortfolioValidator portfolioValidator) {
    this.certificationRepository =
        Objects.requireNonNull(certificationRepository, "certificationRepository must not be null");
    this.portfolioMapper =
        Objects.requireNonNull(portfolioMapper, "portfolioMapper must not be null");
    this.portfolioValidator =
        Objects.requireNonNull(portfolioValidator, "portfolioValidator must not be null");
  }

  @Override
  public CertificationResponse create(UUID ownerId, CertificationRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    portfolioValidator.validateCertificationDates(request.issuedOn(), request.expiresOn());

    Certification certification =
        new Certification(
            ownerId, request.name().trim(), request.issuer().trim(), request.issuedOn());
    certification.setCredentialId(portfolioValidator.normalizeOptionalText(request.credentialId()));
    certification.setCredentialUrl(
        portfolioValidator.normalizeOptionalText(request.credentialUrl()));
    certification.setExpiresOn(request.expiresOn());

    Certification saved = certificationRepository.save(certification);
    return portfolioMapper.toCertificationResponse(saved);
  }

  @Override
  public CertificationResponse update(
      UUID ownerId, UUID certificationId, CertificationRequest request) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(certificationId, "certificationId must not be null");
    Objects.requireNonNull(request, "request must not be null");

    Certification certification = requireOwnedCertification(ownerId, certificationId);
    portfolioValidator.validateCertificationDates(request.issuedOn(), request.expiresOn());

    certification.setName(request.name().trim());
    certification.setIssuer(request.issuer().trim());
    certification.setCredentialId(portfolioValidator.normalizeOptionalText(request.credentialId()));
    certification.setCredentialUrl(
        portfolioValidator.normalizeOptionalText(request.credentialUrl()));
    certification.setIssuedOn(request.issuedOn());
    certification.setExpiresOn(request.expiresOn());
    return portfolioMapper.toCertificationResponse(certification);
  }

  @Override
  public void delete(UUID ownerId, UUID certificationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(certificationId, "certificationId must not be null");
    Certification certification = requireOwnedCertification(ownerId, certificationId);
    certificationRepository.delete(certification);
  }

  @Override
  @Transactional(readOnly = true)
  public CertificationResponse get(UUID ownerId, UUID certificationId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    Objects.requireNonNull(certificationId, "certificationId must not be null");
    return portfolioMapper.toCertificationResponse(
        requireOwnedCertification(ownerId, certificationId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CertificationResponse> list(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    return certificationRepository.findByOwnerIdOrderByIssuedOnDesc(ownerId).stream()
        .map(portfolioMapper::toCertificationResponse)
        .toList();
  }

  private Certification requireOwnedCertification(UUID ownerId, UUID certificationId) {
    return certificationRepository
        .findByIdAndOwnerId(certificationId, ownerId)
        .orElseThrow(() -> new CertificationNotFoundException(certificationId));
  }
}
