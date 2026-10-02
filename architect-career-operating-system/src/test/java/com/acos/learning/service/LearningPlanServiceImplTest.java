package com.acos.learning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.learning.dto.LearningPlanRequest;
import com.acos.learning.dto.LearningPlanResponse;
import com.acos.learning.entity.LearningPlan;
import com.acos.learning.entity.LearningPlanStatus;
import com.acos.learning.event.LearningDomainEventPublisher;
import com.acos.learning.exception.LearningPlanNotFoundException;
import com.acos.learning.mapper.LearningMapper;
import com.acos.learning.repository.LearningPlanRepository;
import com.acos.learning.validator.LearningValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LearningPlanServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class LearningPlanServiceImplTest {

  private static final UUID OWNER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID PLAN_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

  @Mock private LearningPlanRepository learningPlanRepository;
  @Mock private LearningMapper learningMapper;
  @Mock private LearningValidator learningValidator;
  @Mock private LearningDomainEventPublisher learningDomainEventPublisher;

  private LearningPlanServiceImpl learningPlanService;

  @BeforeEach
  void setUp() {
    learningPlanService =
        new LearningPlanServiceImpl(
            learningPlanRepository,
            learningMapper,
            learningValidator,
            learningDomainEventPublisher);
  }

  @Test
  void shouldCreatePlan() {
    LearningPlanRequest request =
        new LearningPlanRequest(
            " System Design ", " desc ", LearningPlanStatus.ACTIVE, LocalDate.of(2026, 12, 31));
    LearningPlanResponse expected =
        new LearningPlanResponse(
            PLAN_ID,
            "System Design",
            "desc",
            LearningPlanStatus.ACTIVE,
            LocalDate.of(2026, 12, 31),
            0,
            0,
            0,
            List.of(),
            Instant.parse("2026-08-04T06:00:00Z"),
            Instant.parse("2026-08-04T06:00:00Z"));

    when(learningValidator.normalizeDescription(" desc ")).thenReturn("desc");
    when(learningPlanRepository.save(any(LearningPlan.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(learningMapper.toPlanResponse(any(LearningPlan.class))).thenReturn(expected);

    LearningPlanResponse response = learningPlanService.create(OWNER_ID, request);

    assertThat(response).isEqualTo(expected);
    ArgumentCaptor<LearningPlan> captor = ArgumentCaptor.forClass(LearningPlan.class);
    verify(learningPlanRepository).save(captor.capture());
    assertThat(captor.getValue().getTitle()).isEqualTo("System Design");
    assertThat(captor.getValue().getStatus()).isEqualTo(LearningPlanStatus.ACTIVE);
  }

  @Test
  void shouldFailWhenPlanMissing() {
    when(learningPlanRepository.findWithDetailsByIdAndOwnerId(PLAN_ID, OWNER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> learningPlanService.get(OWNER_ID, PLAN_ID))
        .isInstanceOf(LearningPlanNotFoundException.class);
  }

  @Test
  void shouldDeleteOwnedPlan() {
    LearningPlan plan = new LearningPlan(OWNER_ID, "Plan", null, LearningPlanStatus.ACTIVE, null);
    when(learningPlanRepository.findByIdAndOwnerId(PLAN_ID, OWNER_ID))
        .thenReturn(Optional.of(plan));

    learningPlanService.delete(OWNER_ID, PLAN_ID);

    verify(learningPlanRepository).delete(plan);
  }
}
