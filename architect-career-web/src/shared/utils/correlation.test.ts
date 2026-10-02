import { describe, expect, it } from 'vitest';
import { createCorrelationId } from './correlation';

describe('createCorrelationId', () => {
  it('returns a non-empty string', () => {
    const id = createCorrelationId();
    expect(id).toBeTruthy();
    expect(typeof id).toBe('string');
  });

  it('returns unique values across calls', () => {
    const a = createCorrelationId();
    const b = createCorrelationId();
    expect(a).not.toBe(b);
  });
});
