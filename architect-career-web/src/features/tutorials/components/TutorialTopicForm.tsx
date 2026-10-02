import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { MenuItem, Stack, TextField } from '@mui/material';
import {
  tutorialTopicSchema,
  type TutorialTopicFormValues,
} from '../schemas/tutorial.schemas';
import type { TutorialTreeNode } from '../types/tutorial.types';

interface TutorialTopicFormProps {
  defaultValues?: TutorialTopicFormValues;
  parentOptions: { id: string; label: string }[];
  onSubmit: (values: TutorialTopicFormValues) => void;
  onRegisterSubmit?: (submit: () => void) => void;
}

const emptyDefaults: TutorialTopicFormValues = {
  title: '',
  slug: '',
  parentId: null,
  sortOrder: null,
};

export function flattenTutorialOptions(
  nodes: TutorialTreeNode[],
  prefix = '',
): { id: string; label: string }[] {
  const options: { id: string; label: string }[] = [];
  for (const node of nodes) {
    const label = prefix ? `${prefix} / ${node.title}` : node.title;
    options.push({ id: node.id, label });
    options.push(...flattenTutorialOptions(node.children, label));
  }
  return options;
}

export function TutorialTopicForm({
  defaultValues = emptyDefaults,
  parentOptions,
  onSubmit,
  onRegisterSubmit,
}: TutorialTopicFormProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TutorialTopicFormValues>({
    resolver: zodResolver(tutorialTopicSchema),
    defaultValues,
  });

  useEffect(() => {
    reset(defaultValues);
  }, [defaultValues, reset]);

  useEffect(() => {
    onRegisterSubmit?.(handleSubmit(onSubmit));
  }, [handleSubmit, onRegisterSubmit, onSubmit]);

  return (
    <Stack spacing={2} component="form" noValidate>
      <Controller
        name="title"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Title"
            required
            error={Boolean(errors.title)}
            helperText={errors.title?.message}
            fullWidth
          />
        )}
      />
      <Controller
        name="slug"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            value={field.value ?? ''}
            label="Slug (optional)"
            error={Boolean(errors.slug)}
            helperText={errors.slug?.message ?? 'Leave blank to generate from title'}
            fullWidth
          />
        )}
      />
      <Controller
        name="parentId"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            value={field.value ?? ''}
            select
            label="Parent topic"
            fullWidth
            onChange={(event) => field.onChange(event.target.value || null)}
          >
            <MenuItem value="">None (root topic)</MenuItem>
            {parentOptions.map((option) => (
              <MenuItem key={option.id} value={option.id}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
        )}
      />
    </Stack>
  );
}
