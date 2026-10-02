import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import { toPageQuery } from '@/shared/api/pageParams';
import type { ApiResponse } from '@/shared/api/types';
import type { PageParams, PageResponse } from '@/shared/types/pagination';
import type {
  LearningPlanRequest,
  LearningPlanResponse,
  LearningPlanSummary,
} from '../types/learning.types';

const PLANS_BASE = '/api/v1/learning/plans';

export const learningPlansApi = {
  async list(params: PageParams = {}): Promise<PageResponse<LearningPlanSummary>> {
    const response = await apiClient.get<ApiResponse<PageResponse<LearningPlanSummary>>>(
      PLANS_BASE,
      { params: toPageQuery(params) },
    );
    return unwrapApiResponse(response);
  },

  async get(planId: string): Promise<LearningPlanResponse> {
    const response = await apiClient.get<ApiResponse<LearningPlanResponse>>(
      `${PLANS_BASE}/${planId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(payload: LearningPlanRequest): Promise<LearningPlanResponse> {
    const response = await apiClient.post<ApiResponse<LearningPlanResponse>>(PLANS_BASE, payload);
    return unwrapApiResponse(response);
  },

  async update(planId: string, payload: LearningPlanRequest): Promise<LearningPlanResponse> {
    const response = await apiClient.put<ApiResponse<LearningPlanResponse>>(
      `${PLANS_BASE}/${planId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(planId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${PLANS_BASE}/${planId}`);
    assertApiSuccess(response);
  },
};
