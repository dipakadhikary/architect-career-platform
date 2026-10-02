import { Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import { companySchema, type CompanyFormValues } from '@/features/career/schemas/career.schemas';
import type { CompanyResponse } from '@/features/career/types/career.types';

interface CompanyFormDialogProps {
  open: boolean;
  company?: CompanyResponse | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: CompanyFormValues) => void;
}

const defaultValues: CompanyFormValues = {
  name: '',
  website: '',
  industry: '',
  location: '',
  notes: '',
};

export function CompanyFormDialog({
  open,
  company,
  loading = false,
  onClose,
  onSubmit,
}: CompanyFormDialogProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CompanyFormValues>({
    resolver: zodResolver(companySchema),
    defaultValues,
  });

  useEffect(() => {
    if (open) {
      reset(
        company
          ? {
              name: company.name,
              website: company.website ?? '',
              industry: company.industry ?? '',
              location: company.location ?? '',
              notes: company.notes ?? '',
            }
          : defaultValues,
      );
    }
  }, [open, company, reset]);

  return (
    <FormDialog
      open={open}
      title={company ? 'Edit Company' : 'Add Company'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="sm"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Controller
          name="name"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Company Name"
              required
              fullWidth
              error={Boolean(errors.name)}
              helperText={errors.name?.message}
            />
          )}
        />
        <Controller
          name="website"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Website"
              fullWidth
              error={Boolean(errors.website)}
              helperText={errors.website?.message}
            />
          )}
        />
        <Controller
          name="industry"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Industry"
              fullWidth
              error={Boolean(errors.industry)}
              helperText={errors.industry?.message}
            />
          )}
        />
        <Controller
          name="location"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Location"
              fullWidth
              error={Boolean(errors.location)}
              helperText={errors.location?.message}
            />
          )}
        />
        <Controller
          name="notes"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Notes"
              fullWidth
              multiline
              minRows={3}
              error={Boolean(errors.notes)}
              helperText={errors.notes?.message}
            />
          )}
        />
      </Stack>
    </FormDialog>
  );
}
