import { describe, expect, it, vi } from 'vitest';
import { withRetry } from './retry';

describe('withRetry', () => {
  it('returns successful result without retrying', async () => {
    const operation = vi.fn().mockResolvedValue('ok');
    await expect(withRetry(operation, { retries: 3 })).resolves.toBe('ok');
    expect(operation).toHaveBeenCalledTimes(1);
  });

  it('retries on configured HTTP statuses then succeeds', async () => {
    const operation = vi
      .fn()
      .mockRejectedValueOnce({ response: { status: 503 } })
      .mockResolvedValueOnce('recovered');

    await expect(withRetry(operation, { retries: 3, delayMs: 1, backoffFactor: 1 })).resolves.toBe(
      'recovered',
    );
    expect(operation).toHaveBeenCalledTimes(2);
  });

  it('does not retry non-retryable failures', async () => {
    const operation = vi.fn().mockRejectedValue({ response: { status: 400 } });
    await expect(withRetry(operation, { retries: 3, delayMs: 1 })).rejects.toEqual({
      response: { status: 400 },
    });
    expect(operation).toHaveBeenCalledTimes(1);
  });
});
