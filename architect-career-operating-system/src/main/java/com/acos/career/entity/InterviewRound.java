package com.acos.career.entity;

/** Round of an interview scheduled for a job application. */
public enum InterviewRound {
  /** Initial recruiter or HR screening. */
  SCREENING,
  /** Technical assessment round. */
  TECHNICAL,
  /** Hiring manager round. */
  MANAGER,
  /** Human resources round. */
  HR,
  /** Final decision round. */
  FINAL,
  /** Round not covered by the standard categories. */
  OTHER
}
