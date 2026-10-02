package com.acos.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.portfolio.dto.PortfolioProjectRequest;
import com.acos.portfolio.dto.PortfolioProjectResponse;
import com.acos.portfolio.entity.PortfolioProject;
import com.acos.portfolio.entity.ProjectStatus;
import com.acos.portfolio.entity.Technology;
import com.acos.portfolio.event.PortfolioDomainEventPublisher;
import com.acos.portfolio.exception.PortfolioProjectNotFoundException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.PortfolioProjectRepository;
import com.acos.portfolio.repository.TechnologyRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit tests for {@link PortfolioProjectServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class PortfolioProjectServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID PROJECT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private PortfolioProjectRepository portfolioProjectRepository;
  @Mock private TechnologyRepository technologyRepository;
  @Mock private PortfolioMapper portfolioMapper;
  @Mock private PortfolioValidator portfolioValidator;
  @Mock private PortfolioDomainEventPublisher portfolioDomainEventPublisher;

  private PortfolioProjectServiceImpl portfolioProjectService;

  @BeforeEach
  void setUp() {
    portfolioProjectService =
        new PortfolioProjectServiceImpl(
            portfolioProjectRepository,
            technologyRepository,
            portfolioMapper,
            portfolioValidator,
            portfolioDomainEventPublisher);
  }

  @Test
  void shouldCreateProjectWithTechnologies() {
    PortfolioProjectRequest request =
        new PortfolioProjectRequest(
            " ACOS Platform ",
            " Career OS ",
            "Detailed description",
            " https://github.com/example/acos ",
            null,
            ProjectStatus.PUBLISHED,
            null,
            null,
            List.of("Java", "Spring Boot"));
    Technology java = new Technology(OWNER_ID, "Java", null);
    Technology spring = new Technology(OWNER_ID, "Spring Boot", null);
    PortfolioProjectResponse expected =
        new PortfolioProjectResponse(
            PROJECT_ID,
            "ACOS Platform",
            "Career OS",
            "Detailed description",
            "https://github.com/example/acos",
            null,
            ProjectStatus.PUBLISHED,
            null,
            null,
            List.of("Java", "Spring Boot"),
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));

    when(portfolioValidator.normalizeTechnologyNames(List.of("Java", "Spring Boot")))
        .thenReturn(List.of("Java", "Spring Boot"));
    when(portfolioValidator.normalizeOptionalText(" https://github.com/example/acos "))
        .thenReturn("https://github.com/example/acos");
    when(portfolioValidator.normalizeOptionalText(null)).thenReturn(null);
    when(technologyRepository.findByOwnerIdAndNameIn(eq(OWNER_ID), any()))
        .thenReturn(List.of(java, spring));
    when(portfolioProjectRepository.save(any(PortfolioProject.class)))
        .thenAnswer(
            invocation -> {
              PortfolioProject project = invocation.getArgument(0);
              ReflectionTestUtils.setField(project, "id", PROJECT_ID);
              return project;
            });
    when(portfolioMapper.toProjectResponse(any(PortfolioProject.class))).thenReturn(expected);

    PortfolioProjectResponse response = portfolioProjectService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    verify(portfolioValidator).validateDescription("Detailed description");
    ArgumentCaptor<PortfolioProject> captor = ArgumentCaptor.forClass(PortfolioProject.class);
    verify(portfolioProjectRepository).save(captor.capture());
    assertThat(captor.getValue().getTitle()).isEqualTo("ACOS Platform");
    assertThat(captor.getValue().getTechnologies()).containsExactlyInAnyOrder(java, spring);
  }

  @Test
  void shouldFailWhenProjectMissing() {
    when(portfolioProjectRepository.findByIdAndOwnerId(PROJECT_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> portfolioProjectService.get(OWNER_ID, PROJECT_ID))
        .isInstanceOf(PortfolioProjectNotFoundException.class);
  }

  @Test
  void shouldDeleteOwnedProject() {
    PortfolioProject project =
        new PortfolioProject(OWNER_ID, "ACOS", "Summary", "Description", ProjectStatus.DRAFT);
    when(portfolioProjectRepository.findByIdAndOwnerId(PROJECT_ID, OWNER_ID))
        .thenReturn(Optional.of(project));

    portfolioProjectService.delete(OWNER_ID, PROJECT_ID);

    verify(portfolioProjectRepository).delete(project);
  }
}
