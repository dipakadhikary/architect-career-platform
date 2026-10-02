import type { AxiosResponse } from 'axios';
import { ApiClientError, type ApiResponse } from './types';

/**
 * Unwraps the backend ApiResponse envelope and throws ApiClientError on failure.
 */
export function unwrapApiResponse<T>(response: AxiosResponse<ApiResponse<T>>): T {
  const body = response.data;

  if (!body.success) {
    throw new ApiClientError(body.error?.message ?? 'Request failed', {
      code: body.error?.code ?? 'INTERNAL_ERROR',
      status: response.status,
      details: body.error?.details ?? [],
      correlationId: body.correlationId,
    });
  }

  if (body.data === null || body.data === undefined) {
    throw new ApiClientError('Empty response payload', {
      code: 'INTERNAL_ERROR',
      status: response.status,
      correlationId: body.correlationId,
    });
  }

  return body.data;
}

/**
 * Asserts a successful ApiResponse that may carry a null payload (e.g. logout).
 */
export function assertApiSuccess(response: AxiosResponse<ApiResponse<unknown>>): void {
  const body = response.data;

  if (!body.success) {
    throw new ApiClientError(body.error?.message ?? 'Request failed', {
      code: body.error?.code ?? 'INTERNAL_ERROR',
      status: response.status,
      details: body.error?.details ?? [],
      correlationId: body.correlationId,
    });
  }
}
