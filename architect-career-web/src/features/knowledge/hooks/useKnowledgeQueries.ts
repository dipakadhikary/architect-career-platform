import { useQuery } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import { knowledgeApi } from '../api/knowledge.api';
import type { KnowledgeNoteListParams } from '../types/knowledge.types';

export const knowledgeKeys = {
  all: ['knowledge'] as const,
  lists: () => [...knowledgeKeys.all, 'list'] as const,
  list: (params: KnowledgeNoteListParams) => [...knowledgeKeys.lists(), params] as const,
  details: () => [...knowledgeKeys.all, 'detail'] as const,
  detail: (noteId: string) => [...knowledgeKeys.details(), noteId] as const,
};

function buildListParams(params: KnowledgeNoteListParams): KnowledgeNoteListParams {
  return {
    page: params.page ?? 0,
    size: params.size ?? moduleConfig.knowledge.defaultPageSize,
    sort: params.sort,
    q: params.q,
  };
}

export function useKnowledgeNotesQuery(params: KnowledgeNoteListParams) {
  const normalized = buildListParams(params);
  const isSearch = Boolean(normalized.q?.trim());

  return useQuery({
    queryKey: knowledgeKeys.list(normalized),
    queryFn: () => (isSearch ? knowledgeApi.search(normalized) : knowledgeApi.list(normalized)),
  });
}

export function useKnowledgeNoteQuery(noteId: string | undefined) {
  return useQuery({
    queryKey: knowledgeKeys.detail(noteId ?? ''),
    queryFn: () => knowledgeApi.getById(noteId!),
    enabled: Boolean(noteId),
  });
}
