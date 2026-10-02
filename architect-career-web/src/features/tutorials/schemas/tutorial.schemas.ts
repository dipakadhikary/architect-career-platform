import { z } from 'zod';

export const tutorialTopicSchema = z.object({
  title: z.string().trim().min(1, 'Title is required').max(200),
  slug: z
    .string()
    .trim()
    .max(200)
    .regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$|^$/, 'Slug must be lowercase kebab-case')
    .optional()
    .or(z.literal('')),
  parentId: z.string().uuid().nullable().optional(),
  sortOrder: z.coerce.number().int().min(0).nullable().optional(),
});

export type TutorialTopicFormValues = z.infer<typeof tutorialTopicSchema>;

export const tutorialConceptSchema = z.object({
  content: z.string().trim().min(1, 'Content is required').max(100_000),
});

export type TutorialConceptFormValues = z.infer<typeof tutorialConceptSchema>;

export const tutorialQuestionSchema = z.object({
  question: z.string().trim().min(1, 'Question is required').max(50_000),
  answer: z.string().trim().min(1, 'Answer is required').max(50_000),
});

export type TutorialQuestionFormValues = z.infer<typeof tutorialQuestionSchema>;
