import { Stack, TextField, MenuItem } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import { learningTopicSchema, type LearningTopicFormValues } from '../schemas/learning.schemas';
import {
  TOPIC_STATUSES,
  type LearningTopicResponse,
  type TopicStatus,
} from '../types/learning.types';
import { formatEnumLabel } from '@/shared/utils/label';

export interface TopicFormHandle {
  submit: () => void;
}

interface TopicFormProps {
  defaultValues?: Partial<LearningTopicFormValues>;
  onSubmit: (values: LearningTopicFormValues) => void;
}

export const TopicForm = forwardRef<TopicFormHandle, TopicFormProps>(function TopicForm(
  { defaultValues, onSubmit },
  ref,
) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<LearningTopicFormValues>({
    resolver: zodResolver(learningTopicSchema),
    defaultValues: {
      title: '',
      description: '',
      status: 'NOT_STARTED',
      sortOrder: 0,
      ...defaultValues,
    },
  });

  useEffect(() => {
    reset({
      title: defaultValues?.title ?? '',
      description: defaultValues?.description ?? '',
      status: defaultValues?.status ?? 'NOT_STARTED',
      sortOrder: defaultValues?.sortOrder ?? 0,
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
        name="status"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            select
            label="Status"
            fullWidth
            error={Boolean(errors.status)}
            helperText={errors.status?.message}
          >
            {TOPIC_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {formatEnumLabel(status)}
              </MenuItem>
            ))}
          </TextField>
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
    </Stack>
  );
});

export function toTopicFormValues(topic: LearningTopicResponse): LearningTopicFormValues {
  return {
    title: topic.title,
    description: topic.description ?? '',
    status: topic.status,
    sortOrder: topic.sortOrder,
  };
}

export function toTopicRequest(values: LearningTopicFormValues) {
  return {
    title: values.title.trim(),
    description: values.description?.trim() || undefined,
    status: values.status as TopicStatus | undefined,
    sortOrder: values.sortOrder,
  };
}
