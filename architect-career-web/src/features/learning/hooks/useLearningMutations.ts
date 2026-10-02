import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { learningPlansApi } from '../api/plans.api';
import { learningMilestonesApi } from '../api/milestones.api';
import { learningTopicsApi } from '../api/topics.api';
import type {
  LearningMilestoneRequest,
  LearningPlanRequest,
  LearningTopicRequest,
  TopicStatus,
} from '../types/learning.types';
import { learningKeys } from './learning.keys';

function invalidatePlanHierarchy(queryClient: ReturnType<typeof useQueryClient>, planId: string) {
  void queryClient.invalidateQueries({ queryKey: learningKeys.plans() });
  void queryClient.invalidateQueries({ queryKey: learningKeys.planDetail(planId) });
  void queryClient.invalidateQueries({ queryKey: learningKeys.milestones(planId) });
}

export function useCreatePlan() {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (payload: LearningPlanRequest) => learningPlansApi.create(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: learningKeys.plans() });
      notification.success('Learning plan created');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to create plan')),
  });
}

export function useUpdatePlan() {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: ({ planId, payload }: { planId: string; payload: LearningPlanRequest }) =>
      learningPlansApi.update(planId, payload),
    onSuccess: (_data, variables) => {
      invalidatePlanHierarchy(queryClient, variables.planId);
      notification.success('Learning plan updated');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to update plan')),
  });
}

export function useDeletePlan() {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (planId: string) => learningPlansApi.remove(planId),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: learningKeys.plans() });
      notification.success('Learning plan deleted');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to delete plan')),
  });
}

export function useCreateMilestone(planId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (payload: LearningMilestoneRequest) =>
      learningMilestonesApi.create(planId, payload),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      notification.success('Milestone created');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to create milestone')),
  });
}

export function useUpdateMilestone(planId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: ({
      milestoneId,
      payload,
    }: {
      milestoneId: string;
      payload: LearningMilestoneRequest;
    }) => learningMilestonesApi.update(planId, milestoneId, payload),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      notification.success('Milestone updated');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to update milestone')),
  });
}

export function useDeleteMilestone(planId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (milestoneId: string) => learningMilestonesApi.remove(planId, milestoneId),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      notification.success('Milestone deleted');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to delete milestone')),
  });
}

export function useCreateTopic(planId: string, milestoneId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (payload: LearningTopicRequest) =>
      learningTopicsApi.create(planId, milestoneId, payload),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      void queryClient.invalidateQueries({
        queryKey: learningKeys.topics(planId, milestoneId),
      });
      notification.success('Topic created');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to create topic')),
  });
}

export function useUpdateTopic(planId: string, milestoneId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: ({ topicId, payload }: { topicId: string; payload: LearningTopicRequest }) =>
      learningTopicsApi.update(planId, milestoneId, topicId, payload),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      void queryClient.invalidateQueries({
        queryKey: learningKeys.topics(planId, milestoneId),
      });
      notification.success('Topic updated');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to update topic')),
  });
}

export function useUpdateTopicStatus(planId: string, milestoneId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: ({ topicId, status }: { topicId: string; status: TopicStatus }) =>
      learningTopicsApi.updateStatus(planId, milestoneId, topicId, { status }),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      void queryClient.invalidateQueries({
        queryKey: learningKeys.topics(planId, milestoneId),
      });
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to update topic status')),
  });
}

export function useDeleteTopic(planId: string, milestoneId: string) {
  const queryClient = useQueryClient();
  const notification = useNotification();

  return useMutation({
    mutationFn: (topicId: string) => learningTopicsApi.remove(planId, milestoneId, topicId),
    onSuccess: () => {
      invalidatePlanHierarchy(queryClient, planId);
      void queryClient.invalidateQueries({
        queryKey: learningKeys.topics(planId, milestoneId),
      });
      notification.success('Topic deleted');
    },
    onError: (error) => notification.error(getErrorMessage(error, 'Failed to delete topic')),
  });
}
