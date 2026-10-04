import { z } from 'zod';
import { moduleConfig } from '@/app/config/module.config';

const { knowledge } = moduleConfig;

const tagNameSchema = z
  .string()
  .trim()
  .min(1, 'Tag cannot be empty')
  .max(knowledge.maxTagLength, `Tag must be at most ${knowledge.maxTagLength} characters`);

export const knowledgeNoteSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title is required')
    .max(knowledge.maxTitleLength, `Title must be at most ${knowledge.maxTitleLength} characters`),
  summary: z
    .string()
    .trim()
    .min(1, 'Summary is required')
    .max(
      knowledge.maxSummaryLength,
      `Summary must be at most ${knowledge.maxSummaryLength} characters`,
    ),
  content: z
    .string()
    .trim()
    .min(1, 'Content is required')
    .max(
      knowledge.maxContentLength,
      `Content must be at most ${knowledge.maxContentLength} characters`,
    ),
  categoryName: z
    .string()
    .trim()
    .max(
      knowledge.maxCategoryLength,
      `Category must be at most ${knowledge.maxCategoryLength} characters`,
    )
    .optional()
    .nullable(),
  tagNames: z
    .array(tagNameSchema)
    .max(knowledge.maxTags, `At most ${knowledge.maxTags} tags allowed`)
    .optional()
    .nullable(),
});

export type KnowledgeNoteFormValues = z.infer<typeof knowledgeNoteSchema>;

export function toKnowledgeNoteRequest(
  values: KnowledgeNoteFormValues,
  expectedVersion?: number | null,
): {
  title: string;
  summary: string;
  content: string;
  categoryName: string | null;
  tagNames: string[] | null;
  expectedVersion?: number | null;
} {
  const categoryName = values.categoryName?.trim();
  const tagNames = values.tagNames?.map((tag) => tag.trim()).filter(Boolean) ?? [];

  return {
    title: values.title.trim(),
    summary: values.summary.trim(),
    content: values.content.trim(),
    categoryName: categoryName ? categoryName : null,
    tagNames: tagNames.length > 0 ? tagNames : null,
    expectedVersion: expectedVersion ?? null,
  };
}

export function toKnowledgeNoteFormValues(note: {
  title: string;
  summary: string;
  content: string;
  category: { name: string } | null;
  tags: string[];
}): KnowledgeNoteFormValues {
  return {
    title: note.title,
    summary: note.summary,
    content: note.content,
    categoryName: note.category?.name ?? '',
    tagNames: note.tags,
  };
}
