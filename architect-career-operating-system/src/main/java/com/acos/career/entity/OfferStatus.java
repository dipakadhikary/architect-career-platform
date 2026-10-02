package com.acos.career.entity;

/** Decision status of a job offer. */
public enum OfferStatus {
  /** Offer awaiting a decision. */
  PENDING,
  /** Offer accepted by the candidate. */
  ACCEPTED,
  /** Offer declined by the candidate. */
  DECLINED,
  /** Offer expired without a decision. */
  EXPIRED,
  /** Offer withdrawn by the employer. */
  WITHDRAWN
}
