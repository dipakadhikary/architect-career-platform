export type LearningPlanStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED';

export type TopicStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';

export interface LearningPlanRequest {
  title: string;
  description?: string;
  status: LearningPlanStatus;
  targetDate?: string | null;
}

export interface LearningPlanSummary {
  id: string;
  title: string;
  description: string | null;
  status: LearningPlanStatus;
  targetDate: string | null;
  progressPercent: number;
  totalTopics: number;
  completedTopics: number;
  createdAt: string;
  updatedAt: string;
}

export interface LearningTopicResponse {
  id: string;
  title: string;
  description: string | null;
  status: TopicStatus;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface LearningMilestoneResponse {
  id: string;
  title: string;
  description: string | null;
  sortOrder: number;
  targetDate: string | null;
  progressPercent: number;
  totalTopics: number;
  completedTopics: number;
  topics: LearningTopicResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface LearningPlanResponse extends LearningPlanSummary {
  milestones: LearningMilestoneResponse[];
}

export interface LearningMilestoneRequest {
  title: string;
  description?: string;
  sortOrder?: number;
  targetDate?: string | null;
}

export interface LearningTopicRequest {
  title: string;
  description?: string;
  status?: TopicStatus;
  sortOrder?: number;
}

export interface TopicStatusUpdateRequest {
  status: TopicStatus;
}

export const LEARNING_PLAN_STATUSES: LearningPlanStatus[] = [
  'DRAFT',
  'ACTIVE',
  'COMPLETED',
  'ARCHIVED',
];

export const TOPIC_STATUSES: TopicStatus[] = ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'];
