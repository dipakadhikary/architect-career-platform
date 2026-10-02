package com.acos.auth.exception;

import com.acos.auth.entity.RoleType;
import com.acos.common.exception.BusinessException;
import com.acos.common.exception.ErrorCode;
import java.io.Serial;

/** Raised when a required role cannot be resolved. */
public class RoleNotFoundException extends BusinessException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Creates a role-not-found exception for the given role type.
   *
   * @param roleType missing role type
   */
  public RoleNotFoundException(RoleType roleType) {
    super(ErrorCode.ROLE_NOT_FOUND, "Role not found: " + roleType);
  }
}
