package com.acos.portfolio.service;

import com.acos.portfolio.dto.CertificationRequest;
import com.acos.portfolio.dto.CertificationResponse;
import java.util.List;
import java.util.UUID;

/** Application service for certification use-cases. */
public interface CertificationService {

  /**
   * Creates a certification for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created certification
   */
  CertificationResponse create(UUID ownerId, CertificationRequest request);

  /**
   * Updates a certification owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param certificationId certification id
   * @param request update payload
   * @return updated certification
   */
  CertificationResponse update(UUID ownerId, UUID certificationId, CertificationRequest request);

  /**
   * Deletes a certification owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param certificationId certification id
   */
  void delete(UUID ownerId, UUID certificationId);

  /**
   * Returns a certification owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param certificationId certification id
   * @return certification details
   */
  CertificationResponse get(UUID ownerId, UUID certificationId);

  /**
   * Lists certifications for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return certifications ordered by issue date descending
   */
  List<CertificationResponse> list(UUID ownerId);
}
