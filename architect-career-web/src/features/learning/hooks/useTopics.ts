import { useQuery } from '@tanstack/react-query';
import { learningTopicsApi } from '../api/topics.api';
import { learningKeys } from './learning.keys';

export function useTopics(planId: string | undefined, milestoneId: string | undefined) {
  return useQuery({
    queryKey: learningKeys.topics(planId ?? '', milestoneId ?? ''),
    queryFn: () => learningTopicsApi.list(planId!, milestoneId!),
    enabled: Boolean(planId && milestoneId),
  });
}
