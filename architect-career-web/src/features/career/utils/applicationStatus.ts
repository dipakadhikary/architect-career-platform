import type { ApplicationStatus } from '@/features/career/types/career.types';

export const TERMINAL_APPLICATION_STATUSES: ApplicationStatus[] = [
  'ACCEPTED',
  'DECLINED',
  'REJECTED',
  'WITHDRAWN',
];

export const APPLICATION_STATUS_TRANSITIONS: Record<ApplicationStatus, ApplicationStatus[]> = {
  DRAFT: ['APPLIED', 'WITHDRAWN'],
  APPLIED: ['SCREENING', 'REJECTED', 'WITHDRAWN'],
  SCREENING: ['TECHNICAL_INTERVIEW', 'REJECTED', 'WITHDRAWN'],
  TECHNICAL_INTERVIEW: ['MANAGER_INTERVIEW', 'REJECTED', 'WITHDRAWN'],
  MANAGER_INTERVIEW: ['HR_INTERVIEW', 'REJECTED', 'WITHDRAWN'],
  HR_INTERVIEW: ['OFFER', 'REJECTED', 'WITHDRAWN'],
  OFFER: ['ACCEPTED', 'DECLINED', 'REJECTED', 'WITHDRAWN'],
  ACCEPTED: [],
  DECLINED: [],
  REJECTED: [],
  WITHDRAWN: [],
};

export const ALL_APPLICATION_STATUSES: ApplicationStatus[] = [
  'DRAFT',
  'APPLIED',
  'SCREENING',
  'TECHNICAL_INTERVIEW',
  'MANAGER_INTERVIEW',
  'HR_INTERVIEW',
  'OFFER',
  'ACCEPTED',
  'DECLINED',
  'REJECTED',
  'WITHDRAWN',
];

export function isTerminalApplicationStatus(status: ApplicationStatus): boolean {
  return TERMINAL_APPLICATION_STATUSES.includes(status);
}

export function getAllowedNextStatuses(current: ApplicationStatus): ApplicationStatus[] {
  return APPLICATION_STATUS_TRANSITIONS[current] ?? [];
}
