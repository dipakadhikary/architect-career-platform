import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { tutorialsApi } from '../api/tutorials.api';
import type { TutorialTopicRequest } from '../types/tutorial.types';

export const tutorialKeys = {
  all: ['tutorials'] as const,
  tree: () => [...tutorialKeys.all, 'tree'] as const,
  topic: (path: string) => [...tutorialKeys.all, 'topic', path] as const,
  concept: (path: string) => [...tutorialKeys.all, 'concept', path] as const,
  questions: (path: string) => [...tutorialKeys.all, 'questions', path] as const,
  search: (q: string) => [...tutorialKeys.all, 'search', q] as const,
};

export function useTutorialTreeQuery() {
  return useQuery({
    queryKey: tutorialKeys.tree(),
    queryFn: () => tutorialsApi.getTree(),
  });
}

export function useTutorialTopicQuery(path: string, enabled = true) {
  return useQuery({
    queryKey: tutorialKeys.topic(path),
    queryFn: () => tutorialsApi.getTopicByPath(path),
    enabled: enabled && Boolean(path),
  });
}

export function useTutorialConceptQuery(path: string, enabled = true) {
  return useQuery({
    queryKey: tutorialKeys.concept(path),
    queryFn: () => tutorialsApi.getConceptByPath(path),
    enabled: enabled && Boolean(path),
  });
}

export function useTutorialQuestionsQuery(path: string, enabled = true) {
  return useQuery({
    queryKey: tutorialKeys.questions(path),
    queryFn: () => tutorialsApi.getQuestionsByPath(path),
    enabled: enabled && Boolean(path),
  });
}

export function useTutorialSearchQuery(q: string, enabled: boolean) {
  return useQuery({
    queryKey: tutorialKeys.search(q),
    queryFn: () => tutorialsApi.search(q),
    enabled: enabled && q.trim().length > 0,
  });
}

export function useCreateTutorialTopicMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: TutorialTopicRequest) => tutorialsApi.createTopic(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: tutorialKeys.tree() });
    },
  });
}

export function useUpdateTutorialTopicMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ topicId, payload }: { topicId: string; payload: TutorialTopicRequest }) =>
      tutorialsApi.updateTopic(topicId, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: tutorialKeys.all });
    },
  });
}

export function useDeleteTutorialTopicMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (topicId: string) => tutorialsApi.deleteTopic(topicId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: tutorialKeys.all });
    },
  });
}

export function useUpsertTutorialConceptMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ topicId, content }: { topicId: string; content: string }) =>
      tutorialsApi.upsertConcept(topicId, content),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: tutorialKeys.all });
    },
  });
}

export function useCreateTutorialQuestionMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      topicId,
      question,
      answer,
    }: {
      topicId: string;
      question: string;
      answer: string;
    }) => tutorialsApi.createQuestion(topicId, { question, answer }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: tutorialKeys.all });
    },
  });
}
