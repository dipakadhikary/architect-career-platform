import { useEffect } from 'react';
import { Stack, TextField } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { FormDialog } from '@/shared/components';
import { achievementFormSchema, type AchievementFormValues } from '../schemas/portfolio.schemas';
import { toAchievementFormValues } from '../schemas/portfolio.form-mappers';
import type { Achievement } from '../types/portfolio.types';

interface AchievementFormDialogProps {
  open: boolean;
  achievement?: Achievement | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: AchievementFormValues) => void;
}

export function AchievementFormDialog({
  open,
  achievement,
  loading = false,
  onClose,
  onSubmit,
}: AchievementFormDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AchievementFormValues>({
    resolver: zodResolver(achievementFormSchema),
    defaultValues: toAchievementFormValues(achievement),
  });

  useEffect(() => {
    if (open) {
      reset(toAchievementFormValues(achievement));
    }
  }, [open, achievement, reset]);

  return (
    <FormDialog
      open={open}
      title={achievement ? 'Edit achievement' : 'New achievement'}
      loading={loading}
      maxWidth="md"
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
    >
      <Stack spacing={2.5} sx={{ pt: 1 }}>
        <TextField
          label="Title"
          fullWidth
          error={Boolean(errors.title)}
          helperText={errors.title?.message}
          {...register('title')}
        />
        <TextField
          label="Description"
          fullWidth
          multiline
          minRows={3}
          error={Boolean(errors.description)}
          helperText={errors.description?.message}
          {...register('description')}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Achieved on"
            type="date"
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
            error={Boolean(errors.achievedOn)}
            helperText={errors.achievedOn?.message}
            {...register('achievedOn')}
          />
          <TextField
            label="Organization"
            fullWidth
            error={Boolean(errors.organization)}
            helperText={errors.organization?.message}
            {...register('organization')}
          />
        </Stack>
      </Stack>
    </FormDialog>
  );
}
