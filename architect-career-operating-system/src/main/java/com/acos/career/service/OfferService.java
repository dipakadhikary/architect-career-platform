package com.acos.career.service;

import com.acos.career.dto.OfferRequest;
import com.acos.career.dto.OfferResponse;
import java.util.List;
import java.util.UUID;

/** Application service for offer use-cases. */
public interface OfferService {

  /**
   * Creates an offer under a job application, always starting as {@code PENDING}.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param request create payload
   * @return created offer
   */
  OfferResponse create(UUID ownerId, UUID applicationId, OfferRequest request);

  /**
   * Updates an offer under a job application, optionally synchronizing the application status when
   * the offer status transitions to {@code ACCEPTED} or {@code DECLINED}.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param offerId offer id
   * @param request update payload
   * @return updated offer
   */
  OfferResponse update(UUID ownerId, UUID applicationId, UUID offerId, OfferRequest request);

  /**
   * Hard-deletes an offer under a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param offerId offer id
   */
  void delete(UUID ownerId, UUID applicationId, UUID offerId);

  /**
   * Returns an offer under a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @param offerId offer id
   * @return offer details
   */
  OfferResponse get(UUID ownerId, UUID applicationId, UUID offerId);

  /**
   * Lists offers for a job application.
   *
   * @param ownerId owning user id
   * @param applicationId application id
   * @return offers ordered by creation time descending
   */
  List<OfferResponse> list(UUID ownerId, UUID applicationId);
}
