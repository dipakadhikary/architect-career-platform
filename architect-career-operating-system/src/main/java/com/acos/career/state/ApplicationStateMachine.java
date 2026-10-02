package com.acos.career.state;

import com.acos.career.entity.ApplicationStatus;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Defines the allowed {@link ApplicationStatus} transition graph for job applications.
 *
 * <p>The lifecycle is linear from {@code DRAFT} through {@code OFFER}, with {@code REJECTED} and
 * {@code WITHDRAWN} reachable from any non-terminal status, and {@code ACCEPTED} / {@code DECLINED}
 * reachable only from {@code OFFER}. All of {@code ACCEPTED}, {@code DECLINED}, {@code REJECTED},
 * and {@code WITHDRAWN} are terminal.
 */
public final class ApplicationStateMachine {

  private static final Map<ApplicationStatus, Set<ApplicationStatus>> TRANSITIONS =
      buildTransitions();

  private ApplicationStateMachine() {}

  /**
   * Reports whether a transition from one status to another is allowed.
   *
   * @param from current status
   * @param to requested target status
   * @return {@code true} when the transition is allowed
   */
  public static boolean isTransitionAllowed(ApplicationStatus from, ApplicationStatus to) {
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");
    return TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
  }

  /**
   * Returns the set of statuses reachable directly from the given status.
   *
   * @param from current status
   * @return allowed next statuses, may be empty for terminal statuses
   */
  public static Set<ApplicationStatus> allowedNextStatuses(ApplicationStatus from) {
    Objects.requireNonNull(from, "from must not be null");
    return TRANSITIONS.getOrDefault(from, Set.of());
  }

  /**
   * Reports whether a status is terminal, i.e. has no outgoing transitions.
   *
   * @param status status to check
   * @return {@code true} when terminal
   */
  public static boolean isTerminal(ApplicationStatus status) {
    Objects.requireNonNull(status, "status must not be null");
    return TRANSITIONS.getOrDefault(status, Set.of()).isEmpty();
  }

  private static Map<ApplicationStatus, Set<ApplicationStatus>> buildTransitions() {
    Map<ApplicationStatus, Set<ApplicationStatus>> transitions =
        new EnumMap<>(ApplicationStatus.class);
    transitions.put(
        ApplicationStatus.DRAFT, Set.of(ApplicationStatus.APPLIED, ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.APPLIED,
        Set.of(
            ApplicationStatus.SCREENING, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.SCREENING,
        Set.of(
            ApplicationStatus.TECHNICAL_INTERVIEW,
            ApplicationStatus.REJECTED,
            ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.TECHNICAL_INTERVIEW,
        Set.of(
            ApplicationStatus.MANAGER_INTERVIEW,
            ApplicationStatus.REJECTED,
            ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.MANAGER_INTERVIEW,
        Set.of(
            ApplicationStatus.HR_INTERVIEW,
            ApplicationStatus.REJECTED,
            ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.HR_INTERVIEW,
        Set.of(ApplicationStatus.OFFER, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN));
    transitions.put(
        ApplicationStatus.OFFER,
        Set.of(
            ApplicationStatus.ACCEPTED,
            ApplicationStatus.DECLINED,
            ApplicationStatus.REJECTED,
            ApplicationStatus.WITHDRAWN));
    transitions.put(ApplicationStatus.ACCEPTED, Set.of());
    transitions.put(ApplicationStatus.DECLINED, Set.of());
    transitions.put(ApplicationStatus.REJECTED, Set.of());
    transitions.put(ApplicationStatus.WITHDRAWN, Set.of());
    return Collections.unmodifiableMap(transitions);
  }
}
