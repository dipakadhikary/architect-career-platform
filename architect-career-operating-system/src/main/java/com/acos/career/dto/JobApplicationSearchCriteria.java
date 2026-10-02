package com.acos.career.dto;

import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.InterviewRound;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Optional filter criteria for job application search.
 *
 * @param companyId optional target company id
 * @param recruiterId optional recruiter id
 * @param status optional application status
 * @param interviewRound optional interview round filter
 * @param appliedFrom optional lower bound on the application date
 * @param appliedTo optional upper bound on the application date
 * @param salaryMin optional lower bound on the salary expectation
 * @param salaryMax optional upper bound on the salary expectation
 * @param keyword optional keyword matched against title, job description, and notes
 */
public record JobApplicationSearchCriteria(
    UUID companyId,
    UUID recruiterId,
    ApplicationStatus status,
    InterviewRound interviewRound,
    LocalDate appliedFrom,
    LocalDate appliedTo,
    BigDecimal salaryMin,
    BigDecimal salaryMax,
    String keyword) {}
