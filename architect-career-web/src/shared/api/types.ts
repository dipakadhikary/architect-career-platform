/**
 * Mirrors com.acos.common.api.ApiResponse / ApiError from the Java backend.
 */
export interface FieldErrorDetail {
  field: string;
  message: string;
  rejectedValue?: unknown;
}

export interface ApiError {
  code: string;
  message: string;
  details: FieldErrorDetail[];
}

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ApiError | null;
  correlationId: string;
  timestamp: string;
}

export class ApiClientError extends Error {
  readonly code: string;
  readonly status: number;
  readonly details: FieldErrorDetail[];
  readonly correlationId?: string;

  constructor(
    message: string,
    options: {
      code: string;
      status: number;
      details?: FieldErrorDetail[];
      correlationId?: string;
      cause?: unknown;
    },
  ) {
    super(message, { cause: options.cause });
    this.name = 'ApiClientError';
    this.code = options.code;
    this.status = options.status;
    this.details = options.details ?? [];
    this.correlationId = options.correlationId;
  }
}
