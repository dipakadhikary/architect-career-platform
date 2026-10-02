package com.acos.career.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.career.entity.ApplicationStatus;
import com.acos.career.entity.Company;
import com.acos.career.entity.JobApplication;
import com.acos.career.entity.Recruiter;
import com.acos.career.specification.JobApplicationSpecifications;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

/** Repository slice tests for job application persistence and search. */
class JobApplicationRepositoryTest extends CareerRepositoryTestSupport {

  @Autowired private JobApplicationRepository jobApplicationRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private RecruiterRepository recruiterRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void shouldPersistApplicationWithDetailsAndSearchByKeyword() {
    User owner =
        userRepository.saveAndFlush(
            new User(
                "career-owner@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Ada",
                "Lovelace"));

    Company company = companyRepository.saveAndFlush(new Company(owner.getId(), "Acme Corp"));
    Recruiter recruiter = new Recruiter(owner.getId(), "Jamie Recruiter");
    recruiter.setCompany(company);
    recruiter = recruiterRepository.saveAndFlush(recruiter);

    JobApplication application =
        new JobApplication(
            owner.getId(), company, "Staff Software Architect", LocalDate.of(2026, 8, 1));
    application.setRecruiter(recruiter);
    application.setSource("LinkedIn");
    application.setStatus(ApplicationStatus.APPLIED);
    JobApplication saved = jobApplicationRepository.saveAndFlush(application);

    JobApplication found =
        jobApplicationRepository
            .findWithDetailsByIdAndOwnerId(saved.getId(), owner.getId())
            .orElseThrow();

    assertThat(found.getTitle()).isEqualTo("Staff Software Architect");
    assertThat(found.getCompany().getName()).isEqualTo("Acme Corp");
    assertThat(found.getRecruiter().getFullName()).isEqualTo("Jamie Recruiter");

    Specification<JobApplication> byKeyword =
        JobApplicationSpecifications.and(
            JobApplicationSpecifications.ownedBy(owner.getId()),
            JobApplicationSpecifications.keyword("architect"));
    Page<JobApplication> byKeywordPage =
        jobApplicationRepository.findAll(byKeyword, PageRequest.of(0, 10));
    Page<JobApplication> listed =
        jobApplicationRepository.findByOwnerIdAndArchived(
            owner.getId(), false, PageRequest.of(0, 10));

    assertThat(byKeywordPage.getContent())
        .extracting(JobApplication::getId)
        .containsExactly(saved.getId());
    assertThat(listed.getTotalElements()).isEqualTo(1);
    assertThat(
            jobApplicationRepository.countByOwnerIdAndArchivedFalseAndStatus(
                owner.getId(), ApplicationStatus.APPLIED))
        .isEqualTo(1L);
  }
}
