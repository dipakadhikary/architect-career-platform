package com.acos.career.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.Company;
import com.acos.career.entity.Interview;
import com.acos.career.entity.InterviewRound;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Recruiter;
import com.acos.career.repository.CareerRepositoryTestSupport;
import com.acos.career.repository.CompanyRepository;
import com.acos.career.repository.InterviewRepository;
import com.acos.career.repository.JobApplicationRepository;
import com.acos.career.repository.RecruiterRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/** Repository-backed tests for {@link JobApplicationSpecifications}. */
class JobApplicationSpecificationsTest extends CareerRepositoryTestSupport {

  @Autowired private JobApplicationRepository jobApplicationRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private RecruiterRepository recruiterRepository;
  @Autowired private InterviewRepository interviewRepository;
  @Autowired private UserRepository userRepository;

  private UUID ownerId;
  private Company company;
  private Recruiter recruiter;
  private JobApplication activeApplication;
  private JobApplication archivedApplication;

  @BeforeEach
  void setUp() {
    User owner =
        userRepository.saveAndFlush(
            new User(
                "specs-owner-" + UUID.randomUUID() + "@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Ada",
                "Lovelace"));
    ownerId = owner.getId();
    company = companyRepository.saveAndFlush(new Company(ownerId, "Acme Corp"));
    recruiter = recruiterRepository.saveAndFlush(new Recruiter(ownerId, "Jamie Recruiter"));

    activeApplication =
        new JobApplication(ownerId, company, "Staff Software Architect", LocalDate.of(2026, 8, 1));
    activeApplication.setStatus(ApplicationStatus.APPLIED);
    activeApplication.setSalaryExpectation(new BigDecimal("220000.00"));
    activeApplication.setRecruiter(recruiter);
    activeApplication = jobApplicationRepository.saveAndFlush(activeApplication);

    interviewRepository.saveAndFlush(
        new Interview(
            activeApplication, InterviewRound.TECHNICAL, Instant.parse("2026-08-20T15:00:00Z")));

    archivedApplication =
        new JobApplication(ownerId, company, "Principal Engineer", LocalDate.of(2026, 6, 1));
    archivedApplication.setStatus(ApplicationStatus.REJECTED);
    archivedApplication.setSalaryExpectation(new BigDecimal("180000.00"));
    archivedApplication.archive();
    archivedApplication = jobApplicationRepository.saveAndFlush(archivedApplication);
  }

  @Test
  void shouldFilterByOwnerAndArchivalState() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.notArchived());

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldFilterByArchivedOnly() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.archivedOnly(true));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(archivedApplication.getId());
  }

  @Test
  void shouldFilterByCompanyAndStatus() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.companyId(company.getId()),
            JobApplicationSpecifications.status(ApplicationStatus.APPLIED));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldFilterBySalaryRange() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.salaryBetween(
                new BigDecimal("200000"), new BigDecimal("250000")));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldFilterByAppliedOnRange() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.appliedBetween(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31)));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldFilterByKeywordAcrossTitleAndNotes() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.keyword("architect"));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldReturnEmptyResultForNonMatchingKeyword() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.keyword("nonexistent-keyword"));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results).isEmpty();
  }

  @Test
  void shouldFilterByRecruiterId() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.recruiterId(recruiter.getId()));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldFilterByInterviewRound() {
    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.interviewRound(InterviewRound.TECHNICAL));

    List<JobApplication> results = jobApplicationRepository.findAll(spec);

    assertThat(results)
        .extracting(JobApplication::getId)
        .containsExactly(activeApplication.getId());
  }

  @Test
  void shouldPageAndSortResults() {
    JobApplication newerApplication =
        new JobApplication(ownerId, company, "Distinguished Engineer", LocalDate.of(2026, 8, 15));
    newerApplication.setStatus(ApplicationStatus.SCREENING);
    newerApplication = jobApplicationRepository.saveAndFlush(newerApplication);

    Specification<JobApplication> spec =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(ownerId),
            JobApplicationSpecifications.notArchived());
    PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "appliedOn"));

    Page<JobApplication> page = jobApplicationRepository.findAll(spec, pageRequest);

    assertThat(page.getTotalElements()).isEqualTo(2L);
    assertThat(page.getContent())
        .extracting(JobApplication::getId)
        .containsExactly(newerApplication.getId());
  }
}
