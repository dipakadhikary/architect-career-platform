import type { PageParams } from '@/shared/types/pagination';

export const learningKeys = {
  all: ['learning'] as const,
  plans: () => [...learningKeys.all, 'plans'] as const,
  plansList: (params: PageParams) => [...learningKeys.plans(), 'list', params] as const,
  planDetail: (planId: string) => [...learningKeys.plans(), 'detail', planId] as const,
  milestones: (planId: string) => [...learningKeys.all, 'milestones', planId] as const,
  topics: (planId: string, milestoneId: string) =>
    [...learningKeys.all, 'topics', planId, milestoneId] as const,
};
