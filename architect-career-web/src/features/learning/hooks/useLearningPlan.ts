import { useQuery } from '@tanstack/react-query';
import { learningPlansApi } from '../api/plans.api';
import { learningKeys } from './learning.keys';

export function useLearningPlan(planId: string | undefined) {
  return useQuery({
    queryKey: learningKeys.planDetail(planId ?? ''),
    queryFn: () => learningPlansApi.get(planId!),
    enabled: Boolean(planId),
  });
}
