import { describe, expect, it } from 'vitest';
import { formatDate, toDateInputValue } from './date';
import { formatEnumLabel } from './label';
import { getErrorMessage, getFieldErrors } from './error';
import { ApiClientError } from '@/shared/api/types';
import { storage } from './storage';

describe('formatDate', () => {
  it('returns em dash for empty values', () => {
    expect(formatDate(null)).toBe('—');
    expect(formatDate(undefined)).toBe('—');
  });

  it('formats ISO dates', () => {
    expect(formatDate('2026-01-15')).toMatch(/2026/);
  });
});

describe('toDateInputValue', () => {
  it('truncates to yyyy-mm-dd', () => {
    expect(toDateInputValue('2026-08-05T12:00:00Z')).toBe('2026-08-05');
  });
});

describe('formatEnumLabel', () => {
  it('title-cases enum values', () => {
    expect(formatEnumLabel('IN_PROGRESS')).toBe('In Progress');
  });
});

describe('getErrorMessage', () => {
  it('prefers field details from ApiClientError', () => {
    const error = new ApiClientError('Request failed', {
      code: 'VALIDATION_FAILED',
      status: 400,
      details: [{ field: 'title', message: 'Required', rejectedValue: null }],
    });
    expect(getErrorMessage(error)).toBe('Required');
  });
});

describe('getFieldErrors', () => {
  it('maps field details', () => {
    const error = new ApiClientError('fail', {
      code: 'VALIDATION_FAILED',
      status: 400,
      details: [{ field: 'email', message: 'Invalid', rejectedValue: 'x' }],
    });
    expect(getFieldErrors(error)).toEqual({ email: 'Invalid' });
  });
});

describe('storage', () => {
  it('round-trips JSON values', () => {
    storage.setJson('acos.test', { ok: true });
    expect(storage.getJson<{ ok: boolean }>('acos.test')).toEqual({ ok: true });
    storage.remove('acos.test');
    expect(storage.getJson('acos.test')).toBeNull();
  });
});
