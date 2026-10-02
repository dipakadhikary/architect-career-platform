import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';
import type {
  LearningTopicRequest,
  LearningTopicResponse,
  TopicStatusUpdateRequest,
} from '../types/learning.types';

function topicsBase(planId: string, milestoneId: string): string {
  return `/api/v1/learning/plans/${planId}/milestones/${milestoneId}/topics`;
}

export const learningTopicsApi = {
  async list(planId: string, milestoneId: string): Promise<LearningTopicResponse[]> {
    const response = await apiClient.get<ApiResponse<LearningTopicResponse[]>>(
      topicsBase(planId, milestoneId),
    );
    return unwrapApiResponse(response);
  },

  async get(planId: string, milestoneId: string, topicId: string): Promise<LearningTopicResponse> {
    const response = await apiClient.get<ApiResponse<LearningTopicResponse>>(
      `${topicsBase(planId, milestoneId)}/${topicId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(
    planId: string,
    milestoneId: string,
    payload: LearningTopicRequest,
  ): Promise<LearningTopicResponse> {
    const response = await apiClient.post<ApiResponse<LearningTopicResponse>>(
      topicsBase(planId, milestoneId),
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(
    planId: string,
    milestoneId: string,
    topicId: string,
    payload: LearningTopicRequest,
  ): Promise<LearningTopicResponse> {
    const response = await apiClient.put<ApiResponse<LearningTopicResponse>>(
      `${topicsBase(planId, milestoneId)}/${topicId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async updateStatus(
    planId: string,
    milestoneId: string,
    topicId: string,
    payload: TopicStatusUpdateRequest,
  ): Promise<LearningTopicResponse> {
    const response = await apiClient.patch<ApiResponse<LearningTopicResponse>>(
      `${topicsBase(planId, milestoneId)}/${topicId}/status`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(planId: string, milestoneId: string, topicId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${topicsBase(planId, milestoneId)}/${topicId}`,
    );
    assertApiSuccess(response);
  },
};
