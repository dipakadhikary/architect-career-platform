package com.acos.career.state;

import com.acos.career.entity.ApplicationStatus;
import com.acos.career.exception.InvalidApplicationStatusTransitionException;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Validates application status transitions against {@link ApplicationStateMachine}. */
@Component
public class ApplicationStateValidator {

  /**
   * Validates that a transition is allowed, throwing when it is not.
   *
   * @param currentStatus current application status
   * @param targetStatus requested target status
   * @throws InvalidApplicationStatusTransitionException when the transition is not allowed
   */
  public void validateTransition(ApplicationStatus currentStatus, ApplicationStatus targetStatus) {
    Objects.requireNonNull(currentStatus, "currentStatus must not be null");
    Objects.requireNonNull(targetStatus, "targetStatus must not be null");
    if (!ApplicationStateMachine.isTransitionAllowed(currentStatus, targetStatus)) {
      throw new InvalidApplicationStatusTransitionException(currentStatus, targetStatus);
    }
  }
}
