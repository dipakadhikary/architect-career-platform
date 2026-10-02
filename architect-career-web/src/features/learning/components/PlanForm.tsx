import { Stack, TextField, MenuItem } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import { learningPlanSchema, type LearningPlanFormValues } from '../schemas/learning.schemas';
import { LEARNING_PLAN_STATUSES, type LearningPlanStatus } from '../types/learning.types';
import { formatEnumLabel } from '@/shared/utils/label';
import { toDateInputValue } from '@/shared/utils/date';

export interface PlanFormHandle {
  submit: () => void;
}

interface PlanFormProps {
  defaultValues?: Partial<LearningPlanFormValues>;
  onSubmit: (values: LearningPlanFormValues) => void;
}

export const PlanForm = forwardRef<PlanFormHandle, PlanFormProps>(function PlanForm(
  { defaultValues, onSubmit },
  ref,
) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<LearningPlanFormValues>({
    resolver: zodResolver(learningPlanSchema),
    defaultValues: {
      title: '',
      description: '',
      status: 'DRAFT',
      targetDate: '',
      ...defaultValues,
    },
  });

  useEffect(() => {
    reset({
      title: defaultValues?.title ?? '',
      description: defaultValues?.description ?? '',
      status: defaultValues?.status ?? 'DRAFT',
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
        name="status"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            select
            label="Status"
            required
            fullWidth
            error={Boolean(errors.status)}
            helperText={errors.status?.message}
          >
            {LEARNING_PLAN_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {formatEnumLabel(status)}
              </MenuItem>
            ))}
          </TextField>
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
});

export function toPlanFormValues(plan: {
  title: string;
  description: string | null;
  status: LearningPlanStatus;
  targetDate: string | null;
}): LearningPlanFormValues {
  return {
    title: plan.title,
    description: plan.description ?? '',
    status: plan.status,
    targetDate: toDateInputValue(plan.targetDate),
  };
}

export function toPlanRequest(values: LearningPlanFormValues) {
  return {
    title: values.title.trim(),
    description: values.description?.trim() || undefined,
    status: values.status as LearningPlanStatus,
    targetDate: values.targetDate || null,
  };
}
