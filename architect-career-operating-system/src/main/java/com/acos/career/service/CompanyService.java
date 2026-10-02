package com.acos.career.service;

import com.acos.career.dto.CompanyRequest;
import com.acos.career.dto.CompanyResponse;
import java.util.List;
import java.util.UUID;

/** Application service for company use-cases. */
public interface CompanyService {

  /**
   * Creates a company for the authenticated owner.
   *
   * @param ownerId owning user id
   * @param request create payload
   * @return created company
   */
  CompanyResponse create(UUID ownerId, CompanyRequest request);

  /**
   * Updates a company owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param companyId company id
   * @param request update payload
   * @return updated company
   */
  CompanyResponse update(UUID ownerId, UUID companyId, CompanyRequest request);

  /**
   * Soft-deletes a company owned by the authenticated user, when it is not referenced by any job
   * application.
   *
   * @param ownerId owning user id
   * @param companyId company id
   */
  void delete(UUID ownerId, UUID companyId);

  /**
   * Returns a company owned by the authenticated user.
   *
   * @param ownerId owning user id
   * @param companyId company id
   * @return company details
   */
  CompanyResponse get(UUID ownerId, UUID companyId);

  /**
   * Lists companies for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return companies ordered by name
   */
  List<CompanyResponse> list(UUID ownerId);
}
