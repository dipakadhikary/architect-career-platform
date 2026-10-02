import { useCallback, useEffect, useMemo, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Box, Chip, Stack, TextField, Typography } from '@mui/material';
import { moduleConfig } from '@/app/config/module.config';
import { knowledgeNoteSchema, type KnowledgeNoteFormValues } from '../schemas/knowledge.schemas';

interface KnowledgeNoteFormProps {
  defaultValues?: KnowledgeNoteFormValues;
  onSubmit: (values: KnowledgeNoteFormValues) => void;
  onRegisterSubmit?: (submit: () => void) => void;
}

const emptyDefaults: KnowledgeNoteFormValues = {
  title: '',
  summary: '',
  content: '',
  categoryName: '',
  tagNames: [],
};

function parseTagsInput(input: string): string[] {
  return input
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean);
}

export function KnowledgeNoteForm({
  defaultValues = emptyDefaults,
  onSubmit,
  onRegisterSubmit,
}: KnowledgeNoteFormProps) {
  const {
    control,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<KnowledgeNoteFormValues>({
    resolver: zodResolver(knowledgeNoteSchema),
    defaultValues,
  });

  useEffect(() => {
    reset(defaultValues);
  }, [defaultValues, reset]);

  useEffect(() => {
    onRegisterSubmit?.(handleSubmit(onSubmit));
  }, [handleSubmit, onRegisterSubmit, onSubmit]);

  const watchedTags = watch('tagNames');
  const tagNames = useMemo(() => watchedTags ?? [], [watchedTags]);
  const [tagInput, setTagInput] = useState('');

  const addTagsFromInput = useCallback(
    (raw: string) => {
      const incoming = parseTagsInput(raw);
      if (incoming.length === 0) return;

      const merged = [...tagNames];
      for (const tag of incoming) {
        if (merged.length >= moduleConfig.knowledge.maxTags) break;
        if (!merged.some((existing) => existing.toLowerCase() === tag.toLowerCase())) {
          merged.push(tag);
        }
      }
      setValue('tagNames', merged, { shouldValidate: true });
      setTagInput('');
    },
    [setValue, tagNames],
  );

  const removeTag = useCallback(
    (tagToRemove: string) => {
      setValue(
        'tagNames',
        tagNames.filter((tag) => tag !== tagToRemove),
        { shouldValidate: true },
      );
    },
    [setValue, tagNames],
  );

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
      <Controller
        name="title"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Title"
            required
            fullWidth
            error={Boolean(errors.title)}
            helperText={errors.title?.message}
            slotProps={{ htmlInput: { maxLength: moduleConfig.knowledge.maxTitleLength } }}
          />
        )}
      />

      <Controller
        name="summary"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Summary"
            required
            fullWidth
            multiline
            minRows={2}
            error={Boolean(errors.summary)}
            helperText={errors.summary?.message}
            slotProps={{ htmlInput: { maxLength: moduleConfig.knowledge.maxSummaryLength } }}
          />
        )}
      />

      <Controller
        name="content"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Content (Markdown)"
            required
            fullWidth
            multiline
            minRows={8}
            error={Boolean(errors.content)}
            helperText={errors.content?.message ?? 'Supports Markdown formatting'}
            slotProps={{ htmlInput: { maxLength: moduleConfig.knowledge.maxContentLength } }}
          />
        )}
      />

      <Controller
        name="categoryName"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            value={field.value ?? ''}
            label="Category"
            fullWidth
            error={Boolean(errors.categoryName)}
            helperText={
              errors.categoryName?.message ?? 'Optional — creates or assigns a category by name'
            }
            slotProps={{ htmlInput: { maxLength: moduleConfig.knowledge.maxCategoryLength } }}
          />
        )}
      />

      <Box>
        <TextField
          value={tagInput}
          onChange={(event) => setTagInput(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Enter' || event.key === ',') {
              event.preventDefault();
              addTagsFromInput(tagInput);
            }
          }}
          onBlur={() => addTagsFromInput(tagInput)}
          label="Tags"
          fullWidth
          placeholder="Type a tag and press Enter or comma"
          error={Boolean(errors.tagNames)}
          helperText={
            errors.tagNames?.message ??
            `Comma-separated tags (max ${moduleConfig.knowledge.maxTags}, ${moduleConfig.knowledge.maxTagLength} chars each)`
          }
        />
        {tagNames.length > 0 ? (
          <Stack direction="row" flexWrap="wrap" gap={1} sx={{ mt: 1.5 }}>
            {tagNames.map((tag) => (
              <Chip key={tag} label={tag} size="small" onDelete={() => removeTag(tag)} />
            ))}
          </Stack>
        ) : (
          <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
            No tags added yet
          </Typography>
        )}
      </Box>
    </Box>
  );
}
