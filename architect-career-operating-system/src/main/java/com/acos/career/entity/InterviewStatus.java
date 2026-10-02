package com.acos.career.entity;

/** Scheduling status of an interview. */
public enum InterviewStatus {
  /** Interview is scheduled. */
  SCHEDULED,
  /** Interview completed. */
  COMPLETED,
  /** Interview cancelled. */
  CANCELLED,
  /** Candidate or interviewer did not attend. */
  NO_SHOW,
  /** Interview rescheduled. */
  RESCHEDULED
}
