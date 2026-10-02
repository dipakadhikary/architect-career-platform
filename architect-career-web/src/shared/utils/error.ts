import { ApiClientError } from '@/shared/api/types';

export function getErrorMessage(error: unknown, fallback = 'Something went wrong'): string {
  if (error instanceof ApiClientError) {
    if (error.details.length > 0) {
      return error.details.map((detail) => detail.message).join('. ');
    }
    return error.message || fallback;
  }
  if (error instanceof Error && error.message) {
    return error.message;
  }
  return fallback;
}

export function getFieldErrors(error: unknown): Record<string, string> {
  if (!(error instanceof ApiClientError)) return {};
  return error.details.reduce<Record<string, string>>((acc, detail) => {
    if (detail.field) {
      acc[detail.field] = detail.message;
    }
    return acc;
  }, {});
}
