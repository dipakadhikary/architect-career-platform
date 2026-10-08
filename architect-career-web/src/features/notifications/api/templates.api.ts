import { apiClient } from '@/shared/api/axios.instance';
import { unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';

export type NotificationChannel = 'EMAIL' | 'SMS';

export interface MessageTemplate {
  id: string;
  code: string;
  channel: NotificationChannel;
  name: string;
  description: string | null;
  subject: string | null;
  htmlBody: string | null;
  textBody: string;
  sampleData: string;
  enabled: boolean;
  version: number;
}

export interface SaveMessageTemplateRequest {
  code?: string;
  channel?: NotificationChannel;
  name: string;
  description?: string;
  subject?: string;
  htmlBody?: string;
  textBody: string;
  sampleData: string;
  enabled: boolean;
  version?: number;
}

const TEMPLATES = '/api/v1/notifications/templates';

export const templateApi = {
  async list(): Promise<MessageTemplate[]> {
    const response = await apiClient.get<ApiResponse<MessageTemplate[]>>(TEMPLATES);
    return unwrapApiResponse(response);
  },

  async create(payload: SaveMessageTemplateRequest): Promise<MessageTemplate> {
    const response = await apiClient.post<ApiResponse<MessageTemplate>>(TEMPLATES, payload);
    return unwrapApiResponse(response);
  },

  async update(id: string, payload: SaveMessageTemplateRequest): Promise<MessageTemplate> {
    const response = await apiClient.put<ApiResponse<MessageTemplate>>(`${TEMPLATES}/${id}`, payload);
    return unwrapApiResponse(response);
  },
};
