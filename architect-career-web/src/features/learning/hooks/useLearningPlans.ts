import { useQuery } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import type { PageParams } from '@/shared/types/pagination';
import { learningPlansApi } from '../api/plans.api';
import { learningKeys } from './learning.keys';

export function useLearningPlans(params: PageParams = {}) {
  const queryParams: PageParams = {
    page: params.page ?? 0,
    size: params.size ?? moduleConfig.learning.defaultPageSize,
    sort: params.sort,
  };

  return useQuery({
    queryKey: learningKeys.plansList(queryParams),
    queryFn: () => learningPlansApi.list(queryParams),
  });
}
