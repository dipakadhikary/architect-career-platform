import { z } from 'zod';
import { moduleConfig } from '@/app/config/module.config';

const { maxPromptLength, maxTranscriptLength } = moduleConfig.ai;
const { maxTitleLength, maxJobDescriptionLength } = moduleConfig.career;

export const knowledgeSearchSchema = z.object({
  query: z.string().trim().min(1, 'Search query is required').max(maxPromptLength),
  limit: z.coerce
    .number()
    .int()
    .min(1, 'Limit must be at least 1')
    .max(50, 'Limit cannot exceed 50'),
});

export const knowledgeSummarizeSchema = z.object({
  noteId: z.string().trim().min(1, 'Note ID is required'),
  content: z
    .string()
    .trim()
    .min(1, 'Content is required')
    .max(moduleConfig.knowledge.maxContentLength),
});

export const learningQuizSchema = z.object({
  topic: z.string().trim().min(1, 'Topic is required').max(maxTitleLength),
  difficulty: z.enum(['BEGINNER', 'INTERMEDIATE', 'ADVANCED']),
  questionCount: z.coerce
    .number()
    .int()
    .min(1, 'At least one question')
    .max(20, 'Maximum 20 questions'),
});

export const recommendNextTopicSchema = z.object({
  planId: z.string().trim().min(1, 'Plan ID is required'),
  completedTopics: z.string().trim().optional().or(z.literal('')),
  goals: z.string().trim().optional().or(z.literal('')),
});

export const evaluateProgressSchema = z.object({
  planId: z.string().trim().min(1, 'Plan ID is required'),
  completedTopics: z.string().trim().optional().or(z.literal('')),
  quizScores: z.string().trim().optional().or(z.literal('')),
});

export const resumeGenerateSchema = z.object({
  targetRole: z.string().trim().min(1, 'Target role is required').max(maxTitleLength),
  experienceHighlights: z.string().trim().min(1, 'Experience highlights are required'),
  skills: z.string().trim().min(1, 'Skills are required'),
});

export const interviewAnalysisSchema = z.object({
  transcript: z.string().trim().min(1, 'Interview transcript is required').max(maxTranscriptLength),
  jobDescription: z
    .string()
    .trim()
    .min(1, 'Job description is required')
    .max(maxJobDescriptionLength),
});

export const coverLetterSchema = z.object({
  targetRole: z.string().trim().min(1, 'Target role is required').max(maxTitleLength),
  companyName: z.string().trim().min(1, 'Company name is required').max(maxTitleLength),
  highlights: z.string().trim().min(1, 'Highlights are required'),
});

export const portfolioReviewSchema = z.object({
  targetRole: z.string().trim().min(1, 'Target role is required').max(maxTitleLength),
  projectIds: z.string().trim().min(1, 'At least one project ID is required'),
});

export const skillGapSchema = z.object({
  targetRole: z.string().trim().min(1, 'Target role is required').max(maxTitleLength),
  currentSkills: z.string().trim().min(1, 'Current skills are required'),
  projectTechnologies: z.string().trim().min(1, 'Project technologies are required'),
});

export type KnowledgeSearchFormValues = z.infer<typeof knowledgeSearchSchema>;
export type KnowledgeSummarizeFormValues = z.infer<typeof knowledgeSummarizeSchema>;
export type LearningQuizFormValues = z.infer<typeof learningQuizSchema>;
export type RecommendNextTopicFormValues = z.infer<typeof recommendNextTopicSchema>;
export type EvaluateProgressFormValues = z.infer<typeof evaluateProgressSchema>;
export type ResumeGenerateFormValues = z.infer<typeof resumeGenerateSchema>;
export type InterviewAnalysisFormValues = z.infer<typeof interviewAnalysisSchema>;
export type CoverLetterFormValues = z.infer<typeof coverLetterSchema>;
export type PortfolioReviewFormValues = z.infer<typeof portfolioReviewSchema>;
export type SkillGapFormValues = z.infer<typeof skillGapSchema>;

export function parseCommaSeparatedList(value?: string): string[] {
  if (!value?.trim()) return [];
  return value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

export function parseCommaSeparatedNumbers(value?: string): number[] {
  if (!value?.trim()) return [];
  return value
    .split(',')
    .map((item) => Number(item.trim()))
    .filter((num) => !Number.isNaN(num));
}
