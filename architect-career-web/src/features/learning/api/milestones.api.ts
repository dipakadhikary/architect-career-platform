import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';
import type { LearningMilestoneRequest, LearningMilestoneResponse } from '../types/learning.types';

function milestonesBase(planId: string): string {
  return `/api/v1/learning/plans/${planId}/milestones`;
}

export const learningMilestonesApi = {
  async list(planId: string): Promise<LearningMilestoneResponse[]> {
    const response = await apiClient.get<ApiResponse<LearningMilestoneResponse[]>>(
      milestonesBase(planId),
    );
    return unwrapApiResponse(response);
  },

  async get(planId: string, milestoneId: string): Promise<LearningMilestoneResponse> {
    const response = await apiClient.get<ApiResponse<LearningMilestoneResponse>>(
      `${milestonesBase(planId)}/${milestoneId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(
    planId: string,
    payload: LearningMilestoneRequest,
  ): Promise<LearningMilestoneResponse> {
    const response = await apiClient.post<ApiResponse<LearningMilestoneResponse>>(
      milestonesBase(planId),
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(
    planId: string,
    milestoneId: string,
    payload: LearningMilestoneRequest,
  ): Promise<LearningMilestoneResponse> {
    const response = await apiClient.put<ApiResponse<LearningMilestoneResponse>>(
      `${milestonesBase(planId)}/${milestoneId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(planId: string, milestoneId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${milestonesBase(planId)}/${milestoneId}`,
    );
    assertApiSuccess(response);
  },
};
