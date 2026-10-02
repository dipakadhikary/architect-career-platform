export interface RetryOptions {
  /** Maximum attempts including the initial call. Default: 3 */
  retries?: number;
  /** Initial delay in milliseconds. Default: 200 */
  delayMs?: number;
  /** Multiplier applied after each failed attempt. Default: 2 */
  backoffFactor?: number;
  /** HTTP status codes that should be retried. Default: 408, 429, 500, 502, 503, 504 */
  retryOnStatuses?: number[];
  /** Optional predicate for custom retry decisions */
  shouldRetry?: (error: unknown, attempt: number) => boolean;
}

const DEFAULT_RETRY_STATUSES = new Set([408, 429, 500, 502, 503, 504]);

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function statusFromError(error: unknown): number | undefined {
  if (
    error &&
    typeof error === 'object' &&
    'response' in error &&
    error.response &&
    typeof error.response === 'object' &&
    'status' in error.response
  ) {
    return Number((error.response as { status: number }).status);
  }
  return undefined;
}

/**
 * Executes an async operation with exponential backoff retry semantics.
 */
export async function withRetry<T>(
  operation: () => Promise<T>,
  options: RetryOptions = {},
): Promise<T> {
  const retries = options.retries ?? 3;
  const delayMs = options.delayMs ?? 200;
  const backoffFactor = options.backoffFactor ?? 2;
  const retryOnStatuses = new Set(options.retryOnStatuses ?? [...DEFAULT_RETRY_STATUSES]);

  let attempt = 0;
  let delay = delayMs;
  let lastError: unknown;

  while (attempt < retries) {
    attempt += 1;
    try {
      return await operation();
    } catch (error) {
      lastError = error;
      const status = statusFromError(error);
      const byStatus = status !== undefined && retryOnStatuses.has(status);
      const byPredicate = options.shouldRetry?.(error, attempt) === true;
      const shouldRetry = attempt < retries && (byStatus || byPredicate);
      if (!shouldRetry) {
        throw error;
      }
      await sleep(delay);
      delay *= backoffFactor;
    }
  }

  throw lastError;
}
