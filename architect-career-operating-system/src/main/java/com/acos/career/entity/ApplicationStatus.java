package com.acos.career.entity;

/** Lifecycle status of a job application. */
public enum ApplicationStatus {
  /** Application draft not yet submitted. */
  DRAFT,
  /** Application submitted. */
  APPLIED,
  /** Recruiter or employer screening in progress. */
  SCREENING,
  /** Technical interview loop in progress. */
  TECHNICAL_INTERVIEW,
  /** Hiring manager interview in progress. */
  MANAGER_INTERVIEW,
  /** HR interview in progress. */
  HR_INTERVIEW,
  /** Offer received. */
  OFFER,
  /** Offer accepted by the candidate. */
  ACCEPTED,
  /** Offer declined by the candidate. */
  DECLINED,
  /** Application rejected. */
  REJECTED,
  /** Application withdrawn by the candidate. */
  WITHDRAWN
}
