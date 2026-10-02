package com.acos.portfolio.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.auth.entity.User;
import com.acos.auth.repository.UserRepository;
import com.acos.portfolio.entity.PortfolioProject;
import com.acos.portfolio.entity.ProjectStatus;
import com.acos.portfolio.entity.Technology;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/** Repository slice tests for portfolio project persistence and search. */
class PortfolioProjectRepositoryTest extends PortfolioRepositoryTestSupport {

  @Autowired private PortfolioProjectRepository portfolioProjectRepository;
  @Autowired private TechnologyRepository technologyRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void shouldPersistProjectWithTechnologiesAndSearchByTitleOrSummary() {
    User owner =
        userRepository.saveAndFlush(
            new User(
                "portfolio-owner@acos.local",
                "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUV",
                "Ada",
                "Lovelace"));

    Technology java =
        technologyRepository.saveAndFlush(new Technology(owner.getId(), "Java", "Language"));
    Technology spring =
        technologyRepository.saveAndFlush(
            new Technology(owner.getId(), "Spring Boot", "Framework"));

    PortfolioProject project =
        new PortfolioProject(
            owner.getId(),
            "ACOS Platform",
            "Career operating system",
            "Detailed portfolio project description",
            ProjectStatus.PUBLISHED);
    project.replaceTechnologies(Set.of(java, spring));
    PortfolioProject saved = portfolioProjectRepository.saveAndFlush(project);

    PortfolioProject found =
        portfolioProjectRepository.findByIdAndOwnerId(saved.getId(), owner.getId()).orElseThrow();

    assertThat(found.getTitle()).isEqualTo("ACOS Platform");
    assertThat(found.getTechnologies())
        .extracting(Technology::getName)
        .containsExactlyInAnyOrder("Java", "Spring Boot");

    Page<PortfolioProject> byTitle =
        portfolioProjectRepository.searchByOwnerIdAndTitleOrSummary(
            owner.getId(), "acos", PageRequest.of(0, 10));
    Page<PortfolioProject> bySummary =
        portfolioProjectRepository.searchByOwnerIdAndTitleOrSummary(
            owner.getId(), "career", PageRequest.of(0, 10));
    Page<PortfolioProject> listed =
        portfolioProjectRepository.findByOwnerId(owner.getId(), PageRequest.of(0, 10));

    assertThat(byTitle.getContent())
        .extracting(PortfolioProject::getId)
        .containsExactly(saved.getId());
    assertThat(bySummary.getContent())
        .extracting(PortfolioProject::getId)
        .containsExactly(saved.getId());
    assertThat(listed.getTotalElements()).isEqualTo(1);
  }
}
