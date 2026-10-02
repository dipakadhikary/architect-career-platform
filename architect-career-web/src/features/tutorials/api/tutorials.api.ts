import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import { toPageQuery } from '@/shared/api/pageParams';
import type { ApiResponse } from '@/shared/api/types';
import type { PageParams } from '@/shared/types/pagination';
import type {
  TutorialConceptResponse,
  TutorialQuestionResponse,
  TutorialQuestionsPageResponse,
  TutorialSearchPageResponse,
  TutorialTopicRequest,
  TutorialTopicResponse,
  TutorialTreeNode,
} from '../types/tutorial.types';

const BASE = '/api/v1/tutorials';

export const tutorialsApi = {
  async getTree(): Promise<TutorialTreeNode[]> {
    const response = await apiClient.get<ApiResponse<TutorialTreeNode[]>>(`${BASE}/tree`);
    return unwrapApiResponse(response);
  },

  async createTopic(payload: TutorialTopicRequest): Promise<TutorialTopicResponse> {
    const response = await apiClient.post<ApiResponse<TutorialTopicResponse>>(
      `${BASE}/topics`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async updateTopic(topicId: string, payload: TutorialTopicRequest): Promise<TutorialTopicResponse> {
    const response = await apiClient.put<ApiResponse<TutorialTopicResponse>>(
      `${BASE}/topics/${topicId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async deleteTopic(topicId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${BASE}/topics/${topicId}`);
    assertApiSuccess(response);
  },

  async getTopicByPath(path: string): Promise<TutorialTopicResponse> {
    const response = await apiClient.get<ApiResponse<TutorialTopicResponse>>(
      `${BASE}/topics/by-path`,
      { params: { path } },
    );
    return unwrapApiResponse(response);
  },

  async getConceptByPath(path: string): Promise<TutorialConceptResponse> {
    const response = await apiClient.get<ApiResponse<TutorialConceptResponse>>(
      `${BASE}/topics/by-path/concept`,
      { params: { path } },
    );
    return unwrapApiResponse(response);
  },

  async upsertConcept(topicId: string, content: string): Promise<TutorialConceptResponse> {
    const response = await apiClient.put<ApiResponse<TutorialConceptResponse>>(
      `${BASE}/topics/${topicId}/concept`,
      { content },
    );
    return unwrapApiResponse(response);
  },

  async getQuestionsByPath(path: string): Promise<TutorialQuestionsPageResponse> {
    const response = await apiClient.get<ApiResponse<TutorialQuestionsPageResponse>>(
      `${BASE}/topics/by-path/questions`,
      { params: { path } },
    );
    return unwrapApiResponse(response);
  },

  async createQuestion(
    topicId: string,
    payload: { question: string; answer: string; sortOrder?: number },
  ): Promise<TutorialQuestionResponse> {
    const response = await apiClient.post<ApiResponse<TutorialQuestionResponse>>(
      `${BASE}/topics/${topicId}/questions`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async updateQuestion(
    questionId: string,
    payload: { question: string; answer: string; sortOrder?: number },
  ): Promise<TutorialQuestionResponse> {
    const response = await apiClient.put<ApiResponse<TutorialQuestionResponse>>(
      `${BASE}/questions/${questionId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async deleteQuestion(questionId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${BASE}/questions/${questionId}`);
    assertApiSuccess(response);
  },

  async search(q: string, params: PageParams = {}): Promise<TutorialSearchPageResponse> {
    const response = await apiClient.get<ApiResponse<TutorialSearchPageResponse>>(`${BASE}/search`, {
      params: { q, ...toPageQuery(params) },
    });
    return unwrapApiResponse(response);
  },
};
