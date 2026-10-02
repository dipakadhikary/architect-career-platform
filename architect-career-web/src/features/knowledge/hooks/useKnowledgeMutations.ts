import { useMutation, useQueryClient } from '@tanstack/react-query';
import { knowledgeApi } from '../api/knowledge.api';
import { knowledgeKeys } from './useKnowledgeQueries';
import type { KnowledgeNotePageResponse, KnowledgeNoteRequest } from '../types/knowledge.types';

export function useCreateKnowledgeNoteMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: KnowledgeNoteRequest) => knowledgeApi.create(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: knowledgeKeys.lists() });
    },
  });
}

export function useUpdateKnowledgeNoteMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ noteId, payload }: { noteId: string; payload: KnowledgeNoteRequest }) =>
      knowledgeApi.update(noteId, payload),
    onSuccess: (note) => {
      void queryClient.invalidateQueries({ queryKey: knowledgeKeys.lists() });
      queryClient.setQueryData(knowledgeKeys.detail(note.id), note);
    },
  });
}

export function useDeleteKnowledgeNoteMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (noteId: string) => knowledgeApi.delete(noteId),
    onMutate: async (noteId) => {
      await queryClient.cancelQueries({ queryKey: knowledgeKeys.lists() });

      const previousLists = queryClient.getQueriesData<KnowledgeNotePageResponse>({
        queryKey: knowledgeKeys.lists(),
      });

      previousLists.forEach(([queryKey, data]) => {
        if (!data) return;
        queryClient.setQueryData<KnowledgeNotePageResponse>(queryKey, {
          ...data,
          content: data.content.filter((note) => note.id !== noteId),
          totalElements: Math.max(0, data.totalElements - 1),
        });
      });

      return { previousLists };
    },
    onError: (_error, _noteId, context) => {
      context?.previousLists.forEach(([queryKey, data]) => {
        queryClient.setQueryData(queryKey, data);
      });
    },
    onSettled: (_data, _error, noteId) => {
      void queryClient.invalidateQueries({ queryKey: knowledgeKeys.lists() });
      queryClient.removeQueries({ queryKey: knowledgeKeys.detail(noteId) });
    },
  });
}
