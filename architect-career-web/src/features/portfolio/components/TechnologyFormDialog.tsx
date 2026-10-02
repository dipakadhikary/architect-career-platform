import { useEffect } from 'react';
import { Stack, TextField } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { FormDialog } from '@/shared/components';
import { technologyFormSchema, type TechnologyFormValues } from '../schemas/portfolio.schemas';
import { toTechnologyFormValues } from '../schemas/portfolio.form-mappers';
import type { Technology } from '../types/portfolio.types';

interface TechnologyFormDialogProps {
  open: boolean;
  technology?: Technology | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: TechnologyFormValues) => void;
}

export function TechnologyFormDialog({
  open,
  technology,
  loading = false,
  onClose,
  onSubmit,
}: TechnologyFormDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TechnologyFormValues>({
    resolver: zodResolver(technologyFormSchema),
    defaultValues: toTechnologyFormValues(technology),
  });

  useEffect(() => {
    if (open) {
      reset(toTechnologyFormValues(technology));
    }
  }, [open, technology, reset]);

  return (
    <FormDialog
      open={open}
      title={technology ? 'Edit technology' : 'New technology'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
    >
      <Stack spacing={2.5} sx={{ pt: 1 }}>
        <TextField
          label="Name"
          fullWidth
          error={Boolean(errors.name)}
          helperText={errors.name?.message}
          {...register('name')}
        />
        <TextField
          label="Category"
          fullWidth
          error={Boolean(errors.category)}
          helperText={errors.category?.message}
          {...register('category')}
        />
      </Stack>
    </FormDialog>
  );
}
