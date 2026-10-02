package com.acos.career.entity;

/** Business action recorded in the career audit trail. */
public enum CareerAuditAction {
  /** A job application was created. */
  APPLICATION_CREATED,
  /** A job application was updated. */
  APPLICATION_UPDATED,
  /** A job application was archived. */
  APPLICATION_ARCHIVED,
  /** An interview was scheduled. */
  INTERVIEW_SCHEDULED,
  /** An interview was updated. */
  INTERVIEW_UPDATED,
  /** An interview was marked completed. */
  INTERVIEW_COMPLETED,
  /** An offer was created. */
  OFFER_CREATED,
  /** An offer was updated. */
  OFFER_UPDATED,
  /** An offer was accepted. */
  OFFER_ACCEPTED,
  /** An offer was declined. */
  OFFER_DECLINED,
  /** An application status changed. */
  STATUS_CHANGED
}
