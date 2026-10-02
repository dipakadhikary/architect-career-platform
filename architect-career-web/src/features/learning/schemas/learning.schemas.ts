import { z } from 'zod';
import { moduleConfig } from '@/app/config/module.config';
import { LEARNING_PLAN_STATUSES, TOPIC_STATUSES } from '../types/learning.types';

const { maxTitleLength, maxDescriptionLength } = moduleConfig.learning;

export const learningPlanSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title is required')
    .max(maxTitleLength, `Title must be at most ${maxTitleLength} characters`),
  description: z
    .string()
    .trim()
    .max(maxDescriptionLength, `Description must be at most ${maxDescriptionLength} characters`)
    .optional()
    .or(z.literal('')),
  status: z.enum(LEARNING_PLAN_STATUSES as [string, ...string[]]),
  targetDate: z.string().optional().or(z.literal('')),
});

export const learningMilestoneSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title is required')
    .max(maxTitleLength, `Title must be at most ${maxTitleLength} characters`),
  description: z
    .string()
    .trim()
    .max(maxDescriptionLength, `Description must be at most ${maxDescriptionLength} characters`)
    .optional()
    .or(z.literal('')),
  sortOrder: z.coerce
    .number()
    .int('Sort order must be a whole number')
    .min(0, 'Sort order cannot be negative')
    .optional(),
  targetDate: z.string().optional().or(z.literal('')),
});

export const learningTopicSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title is required')
    .max(maxTitleLength, `Title must be at most ${maxTitleLength} characters`),
  description: z
    .string()
    .trim()
    .max(maxDescriptionLength, `Description must be at most ${maxDescriptionLength} characters`)
    .optional()
    .or(z.literal('')),
  status: z.enum(TOPIC_STATUSES as [string, ...string[]]).optional(),
  sortOrder: z.coerce
    .number()
    .int('Sort order must be a whole number')
    .min(0, 'Sort order cannot be negative')
    .optional(),
});

export type LearningPlanFormValues = z.infer<typeof learningPlanSchema>;
export type LearningMilestoneFormValues = z.infer<typeof learningMilestoneSchema>;
export type LearningTopicFormValues = z.infer<typeof learningTopicSchema>;
