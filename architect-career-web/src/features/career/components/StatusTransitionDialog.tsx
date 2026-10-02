import { FormControl, InputLabel, MenuItem, Select, Stack, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect } from 'react';
import { FormDialog } from '@/shared/components';
import {
  statusTransitionSchema,
  type StatusTransitionFormValues,
} from '@/features/career/schemas/career.schemas';
import type { ApplicationStatus } from '@/features/career/types/career.types';
import { formatEnumLabel } from '@/shared/utils/label';

interface StatusTransitionDialogProps {
  open: boolean;
  currentStatus: ApplicationStatus;
  allowedStatuses: ApplicationStatus[];
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: StatusTransitionFormValues) => void;
}

export function StatusTransitionDialog({
  open,
  currentStatus,
  allowedStatuses,
  loading = false,
  onClose,
  onSubmit,
}: StatusTransitionDialogProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<StatusTransitionFormValues>({
    resolver: zodResolver(statusTransitionSchema),
    defaultValues: {
      newStatus: allowedStatuses[0] ?? currentStatus,
      comments: '',
    },
  });

  useEffect(() => {
    if (open) {
      reset({
        newStatus: allowedStatuses[0] ?? currentStatus,
        comments: '',
      });
    }
  }, [open, allowedStatuses, currentStatus, reset]);

  return (
    <FormDialog
      open={open}
      title="Update Application Status"
      submitLabel="Update Status"
      loading={loading}
      onClose={onClose}
      onSubmit={handleSubmit(onSubmit)}
      maxWidth="sm"
    >
      <Stack spacing={2} sx={{ pt: 1 }}>
        <TextField
          label="Current Status"
          value={formatEnumLabel(currentStatus)}
          fullWidth
          disabled
        />
        <Controller
          name="newStatus"
          control={control}
          render={({ field }) => (
            <FormControl fullWidth required error={Boolean(errors.newStatus)}>
              <InputLabel id="new-status-label">New Status</InputLabel>
              <Select {...field} labelId="new-status-label" label="New Status *">
                {allowedStatuses.map((status) => (
                  <MenuItem key={status} value={status}>
                    {formatEnumLabel(status)}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
        <Controller
          name="comments"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Comments"
              fullWidth
              multiline
              minRows={3}
              error={Boolean(errors.comments)}
              helperText={errors.comments?.message}
            />
          )}
        />
      </Stack>
    </FormDialog>
  );
}
