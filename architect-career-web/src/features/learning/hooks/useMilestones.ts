import { useQuery } from '@tanstack/react-query';
import { learningMilestonesApi } from '../api/milestones.api';
import { learningKeys } from './learning.keys';

export function useMilestones(planId: string | undefined) {
  return useQuery({
    queryKey: learningKeys.milestones(planId ?? ''),
    queryFn: () => learningMilestonesApi.list(planId!),
    enabled: Boolean(planId),
  });
}
