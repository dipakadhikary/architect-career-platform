package com.acos.career.state;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.career.entity.ApplicationStatus;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for {@link ApplicationStateMachine}. */
class ApplicationStateMachineTest {

  private static final Set<ApplicationStatus> TERMINAL_STATUSES =
      EnumSet.of(
          ApplicationStatus.ACCEPTED,
          ApplicationStatus.DECLINED,
          ApplicationStatus.REJECTED,
          ApplicationStatus.WITHDRAWN);

  static Stream<Arguments> allowedTransitions() {
    return Stream.of(
        Arguments.of(ApplicationStatus.DRAFT, ApplicationStatus.APPLIED),
        Arguments.of(ApplicationStatus.DRAFT, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING),
        Arguments.of(ApplicationStatus.APPLIED, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.APPLIED, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.SCREENING, ApplicationStatus.TECHNICAL_INTERVIEW),
        Arguments.of(ApplicationStatus.SCREENING, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.SCREENING, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.TECHNICAL_INTERVIEW, ApplicationStatus.MANAGER_INTERVIEW),
        Arguments.of(ApplicationStatus.TECHNICAL_INTERVIEW, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.TECHNICAL_INTERVIEW, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.MANAGER_INTERVIEW, ApplicationStatus.HR_INTERVIEW),
        Arguments.of(ApplicationStatus.MANAGER_INTERVIEW, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.MANAGER_INTERVIEW, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.HR_INTERVIEW, ApplicationStatus.OFFER),
        Arguments.of(ApplicationStatus.HR_INTERVIEW, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.HR_INTERVIEW, ApplicationStatus.WITHDRAWN),
        Arguments.of(ApplicationStatus.OFFER, ApplicationStatus.ACCEPTED),
        Arguments.of(ApplicationStatus.OFFER, ApplicationStatus.DECLINED),
        Arguments.of(ApplicationStatus.OFFER, ApplicationStatus.REJECTED),
        Arguments.of(ApplicationStatus.OFFER, ApplicationStatus.WITHDRAWN));
  }

  static Stream<Arguments> invalidTransitions() {
    return Arrays.stream(ApplicationStatus.values())
        .flatMap(
            from ->
                Arrays.stream(ApplicationStatus.values())
                    .filter(to -> !ApplicationStateMachine.allowedNextStatuses(from).contains(to))
                    .map(to -> Arguments.of(from, to)));
  }

  @ParameterizedTest
  @MethodSource("allowedTransitions")
  void shouldAllowValidTransitions(ApplicationStatus from, ApplicationStatus to) {
    assertThat(ApplicationStateMachine.isTransitionAllowed(from, to)).isTrue();
    assertThat(ApplicationStateMachine.allowedNextStatuses(from)).contains(to);
  }

  @ParameterizedTest
  @MethodSource("invalidTransitions")
  void shouldRejectInvalidTransitions(ApplicationStatus from, ApplicationStatus to) {
    assertThat(ApplicationStateMachine.isTransitionAllowed(from, to)).isFalse();
  }

  @Test
  void shouldRejectSameStatusTransitions() {
    for (ApplicationStatus status : ApplicationStatus.values()) {
      assertThat(ApplicationStateMachine.isTransitionAllowed(status, status)).isFalse();
    }
  }

  @Test
  void shouldTreatTerminalStatusesAsHavingNoOutgoingTransitions() {
    for (ApplicationStatus status : TERMINAL_STATUSES) {
      assertThat(ApplicationStateMachine.isTerminal(status)).isTrue();
      assertThat(ApplicationStateMachine.allowedNextStatuses(status)).isEmpty();
      for (ApplicationStatus target : ApplicationStatus.values()) {
        assertThat(ApplicationStateMachine.isTransitionAllowed(status, target)).isFalse();
      }
    }
  }

  @Test
  void shouldTreatNonTerminalStatusesAsHavingOutgoingTransitions() {
    for (ApplicationStatus status : ApplicationStatus.values()) {
      if (!TERMINAL_STATUSES.contains(status)) {
        assertThat(ApplicationStateMachine.isTerminal(status)).isFalse();
        assertThat(ApplicationStateMachine.allowedNextStatuses(status)).isNotEmpty();
      }
    }
  }
}
