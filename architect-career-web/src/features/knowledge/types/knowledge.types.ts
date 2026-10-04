import type { PageParams, PageResponse } from '@/shared/types/pagination';

export interface KnowledgeCategory {
  id: string;
  name: string;
  description: string | null;
}

export interface KnowledgeNoteResponse {
  id: string;
  title: string;
  summary: string;
  content: string;
  category: KnowledgeCategory | null;
  tags: string[];
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface KnowledgeNoteRequest {
  title: string;
  summary: string;
  content: string;
  categoryName?: string | null;
  tagNames?: string[] | null;
  expectedVersion?: number | null;
}

export interface KnowledgeNoteListParams extends PageParams {
  q?: string;
}

export type KnowledgeNotePageResponse = PageResponse<KnowledgeNoteResponse>;
