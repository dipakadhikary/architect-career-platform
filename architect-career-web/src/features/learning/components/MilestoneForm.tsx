import { Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import {
  learningMilestoneSchema,
  type LearningMilestoneFormValues,
} from '../schemas/learning.schemas';
import { toDateInputValue } from '@/shared/utils/date';
import type { LearningMilestoneResponse } from '../types/learning.types';

export interface MilestoneFormHandle {
  submit: () => void;
}

interface MilestoneFormProps {
  defaultValues?: Partial<LearningMilestoneFormValues>;
  onSubmit: (values: LearningMilestoneFormValues) => void;
}

export const MilestoneForm = forwardRef<MilestoneFormHandle, MilestoneFormProps>(
  function MilestoneForm({ defaultValues, onSubmit }, ref) {
    const {
      control,
      handleSubmit,
      reset,
      formState: { errors },
    } = useForm<LearningMilestoneFormValues>({
      resolver: zodResolver(learningMilestoneSchema),
      defaultValues: {
        title: '',
        description: '',
        sortOrder: 0,
        targetDate: '',
        ...defaultValues,
      },
    });

    useEffect(() => {
      reset({
        title: defaultValues?.title ?? '',
        description: defaultValues?.description ?? '',
        sortOrder: defaultValues?.sortOrder ?? 0,
        targetDate: defaultValues?.targetDate ?? '',
      });
    }, [defaultValues, reset]);

    useImperativeHandle(ref, () => ({
      submit: () => {
        void handleSubmit(onSubmit)();
      },
    }));

    return (
      <Stack spacing={2.5} sx={{ pt: 1 }}>
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
              autoFocus
            />
          )}
        />
        <Controller
          name="description"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Description"
              fullWidth
              multiline
              minRows={3}
              error={Boolean(errors.description)}
              helperText={errors.description?.message}
            />
          )}
        />
        <Controller
          name="sortOrder"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Sort order"
              type="number"
              fullWidth
              error={Boolean(errors.sortOrder)}
              helperText={errors.sortOrder?.message}
            />
          )}
        />
        <Controller
          name="targetDate"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Target date"
              type="date"
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
              error={Boolean(errors.targetDate)}
              helperText={errors.targetDate?.message}
            />
          )}
        />
      </Stack>
    );
  },
);

export function toMilestoneFormValues(
  milestone: LearningMilestoneResponse,
): LearningMilestoneFormValues {
  return {
    title: milestone.title,
    description: milestone.description ?? '',
    sortOrder: milestone.sortOrder,
    targetDate: toDateInputValue(milestone.targetDate),
  };
}

export function toMilestoneRequest(values: LearningMilestoneFormValues) {
  return {
    title: values.title.trim(),
    description: values.description?.trim() || undefined,
    sortOrder: values.sortOrder,
    targetDate: values.targetDate || null,
  };
}
