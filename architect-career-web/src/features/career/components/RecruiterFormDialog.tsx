import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import {
  recruiterSchema,
  type RecruiterFormValues,
} from '@/features/career/schemas/career.schemas';
import type { CompanyResponse, RecruiterResponse } from '@/features/career/types/career.types';
import { formatEnumLabel } from '@/shared/utils/label';
import { toDateInputValue } from '@/shared/utils/date';

interface RecruiterFormDialogProps {
  open: boolean;
  recruiter?: RecruiterResponse | null;
  companies: CompanyResponse[];
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: RecruiterFormValues) => void;
}

const defaultValues: RecruiterFormValues = {
  companyId: '',
  fullName: '',
  email: '',
  phone: '',
  linkedInUrl: '',
  lastContactDate: '',
  nextFollowUpDate: '',
  status: 'ACTIVE',
  notes: '',
};

export function RecruiterFormDialog({
  open,
  recruiter,
  companies,
  loading = false,
  onClose,
  onSubmit,
}: RecruiterFormDialogProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<RecruiterFormValues>({
    resolver: zodResolver(recruiterSchema),
    defaultValues,
  });

  useEffect(() => {
    if (open) {
      reset(
        recruiter
          ? {
              companyId: recruiter.companyId ?? '',
              fullName: recruiter.fullName,
              email: recruiter.email ?? '',
              phone: recruiter.phone ?? '',
              linkedInUrl: recruiter.linkedInUrl ?? '',
              lastContactDate: toDateInputValue(recruiter.lastContactDate),
              nextFollowUpDate: toDateInputValue(recruiter.nextFollowUpDate),
              status: recruiter.status,
              notes: recruiter.notes ?? '',
            }
          : defaultValues,
      );
    }
  }, [open, recruiter, reset]);

  return (
    <FormDialog
      open={open}
      title={recruiter ? 'Edit Recruiter' : 'Add Recruiter'}
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="sm"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <Controller
          name="companyId"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth>
              <InputLabel id="recruiter-company-label">Company</InputLabel>
              <Select {...field} labelId="recruiter-company-label" label="Company">
                <MenuItem value="">None</MenuItem>
                {companies.map((company) => (
                  <MenuItem key={company.id} value={company.id}>
                    {company.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
        <Controller
          name="fullName"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Full Name"
              required
              fullWidth
              error={Boolean(errors.fullName)}
              helperText={errors.fullName?.message}
            />
          )}
        />
        <Controller
          name="email"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Email"
              fullWidth
              error={Boolean(errors.email)}
              helperText={errors.email?.message}
            />
          )}
        />
        <Controller
          name="phone"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Phone"
              fullWidth
              error={Boolean(errors.phone)}
              helperText={errors.phone?.message}
            />
          )}
        />
        <Controller
          name="linkedInUrl"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="LinkedIn URL"
              fullWidth
              error={Boolean(errors.linkedInUrl)}
              helperText={errors.linkedInUrl?.message}
            />
          )}
        />
        <Controller
          name="status"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth>
              <InputLabel id="recruiter-status-label">Status</InputLabel>
              <Select {...field} labelId="recruiter-status-label" label="Status">
                {(['ACTIVE', 'INACTIVE', 'DO_NOT_CONTACT'] as const).map((status) => (
                  <MenuItem key={status} value={status}>
                    {formatEnumLabel(status)}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
        <Controller
          name="lastContactDate"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Last Contact Date"
              type="date"
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
            />
          )}
        />
        <Controller
          name="nextFollowUpDate"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Next Follow-up Date"
              type="date"
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
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
