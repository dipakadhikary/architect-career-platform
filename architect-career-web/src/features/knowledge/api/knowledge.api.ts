import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import { toPageQuery } from '@/shared/api/pageParams';
import type { ApiResponse } from '@/shared/api/types';
import type { PageParams } from '@/shared/types/pagination';
import type {
  KnowledgeNoteListParams,
  KnowledgeNotePageResponse,
  KnowledgeNoteRequest,
  KnowledgeNoteResponse,
} from '../types/knowledge.types';

const KNOWLEDGE_BASE = '/api/v1/knowledge/notes';

export const knowledgeApi = {
  async create(payload: KnowledgeNoteRequest): Promise<KnowledgeNoteResponse> {
    const response = await apiClient.post<ApiResponse<KnowledgeNoteResponse>>(
      KNOWLEDGE_BASE,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(noteId: string, payload: KnowledgeNoteRequest): Promise<KnowledgeNoteResponse> {
    const response = await apiClient.put<ApiResponse<KnowledgeNoteResponse>>(
      `${KNOWLEDGE_BASE}/${noteId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async delete(noteId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${KNOWLEDGE_BASE}/${noteId}`);
    assertApiSuccess(response);
  },

  async getById(noteId: string): Promise<KnowledgeNoteResponse> {
    const response = await apiClient.get<ApiResponse<KnowledgeNoteResponse>>(
      `${KNOWLEDGE_BASE}/${noteId}`,
    );
    return unwrapApiResponse(response);
  },

  async list(params: PageParams = {}): Promise<KnowledgeNotePageResponse> {
    const response = await apiClient.get<ApiResponse<KnowledgeNotePageResponse>>(KNOWLEDGE_BASE, {
      params: toPageQuery(params),
    });
    return unwrapApiResponse(response);
  },

  async search(params: KnowledgeNoteListParams): Promise<KnowledgeNotePageResponse> {
    const { q, ...pageParams } = params;
    const response = await apiClient.get<ApiResponse<KnowledgeNotePageResponse>>(
      `${KNOWLEDGE_BASE}/search`,
      {
        params: {
          q: q?.trim() ?? '',
          ...toPageQuery(pageParams),
        },
      },
    );
    return unwrapApiResponse(response);
  },
};
