package com.acos.common.exception;

/** Exception raised when a requested resource cannot be found. */
public class ResourceNotFoundException extends BusinessException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a not-found exception with an explicit message.
   *
   * @param message not-found message
   */
  public ResourceNotFoundException(String message) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message);
  }

  /**
   * Creates a not-found exception for a resource type and identifier.
   *
   * @param resourceType resource type name
   * @param identifier resource identifier
   */
  public ResourceNotFoundException(String resourceType, Object identifier) {
    super(
        ErrorCode.RESOURCE_NOT_FOUND,
        "%s not found with identifier '%s'".formatted(resourceType, identifier));
  }
}
