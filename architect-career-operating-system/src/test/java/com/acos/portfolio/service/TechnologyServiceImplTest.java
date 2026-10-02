package com.acos.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.portfolio.dto.TechnologyRequest;
import com.acos.portfolio.dto.TechnologyResponse;
import com.acos.portfolio.entity.Technology;
import com.acos.portfolio.exception.DuplicatePortfolioNameException;
import com.acos.portfolio.mapper.PortfolioMapper;
import com.acos.portfolio.repository.TechnologyRepository;
import com.acos.portfolio.validator.PortfolioValidator;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link TechnologyServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class TechnologyServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID TECHNOLOGY_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private TechnologyRepository technologyRepository;
  @Mock private PortfolioMapper portfolioMapper;
  @Mock private PortfolioValidator portfolioValidator;

  private TechnologyServiceImpl technologyService;

  @BeforeEach
  void setUp() {
    technologyService =
        new TechnologyServiceImpl(technologyRepository, portfolioMapper, portfolioValidator);
  }

  @Test
  void shouldCreateTechnology() {
    TechnologyRequest request = new TechnologyRequest(" Spring Boot ", " Backend ");
    TechnologyResponse expected =
        new TechnologyResponse(
            TECHNOLOGY_ID,
            "Spring Boot",
            "Backend",
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));

    when(technologyRepository.existsByOwnerIdAndName(OWNER_ID, "Spring Boot")).thenReturn(false);
    when(portfolioValidator.normalizeOptionalText(" Backend ")).thenReturn("Backend");
    when(technologyRepository.save(any(Technology.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(portfolioMapper.toTechnologyResponse(any(Technology.class))).thenReturn(expected);

    TechnologyResponse response = technologyService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<Technology> captor = ArgumentCaptor.forClass(Technology.class);
    verify(technologyRepository).save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Spring Boot");
    assertThat(captor.getValue().getCategory()).isEqualTo("Backend");
  }

  @Test
  void shouldRejectDuplicateTechnologyName() {
    TechnologyRequest request = new TechnologyRequest("Java", null);
    when(technologyRepository.existsByOwnerIdAndName(OWNER_ID, "Java")).thenReturn(true);

    assertThatThrownBy(() -> technologyService.create(OWNER_ID, request))
        .isInstanceOf(DuplicatePortfolioNameException.class)
        .hasMessageContaining("Java");
    verify(technologyRepository, never()).save(any());
  }
}
