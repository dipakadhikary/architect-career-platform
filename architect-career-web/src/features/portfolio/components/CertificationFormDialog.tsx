import { useEffect } from 'react';
import { Stack, TextField } from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { FormDialog } from '@/shared/components';
import {
  certificationFormSchema,
  type CertificationFormValues,
} from '../schemas/portfolio.schemas';
import { toCertificationFormValues } from '../schemas/portfolio.form-mappers';
import type { Certification } from '../types/portfolio.types';

interface CertificationFormDialogProps {
  open: boolean;
  certification?: Certification | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: CertificationFormValues) => void;
}

export function CertificationFormDialog({
  open,
  certification,
  loading = false,
  onClose,
  onSubmit,
}: CertificationFormDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CertificationFormValues>({
    resolver: zodResolver(certificationFormSchema),
    defaultValues: toCertificationFormValues(certification),
  });

  useEffect(() => {
    if (open) {
      reset(toCertificationFormValues(certification));
    }
  }, [open, certification, reset]);

  return (
    <FormDialog
      open={open}
      title={certification ? 'Edit certification' : 'New certification'}
      loading={loading}
      maxWidth="md"
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
          label="Issuer"
          fullWidth
          error={Boolean(errors.issuer)}
          helperText={errors.issuer?.message}
          {...register('issuer')}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Credential ID"
            fullWidth
            error={Boolean(errors.credentialId)}
            helperText={errors.credentialId?.message}
            {...register('credentialId')}
          />
          <TextField
            label="Credential URL"
            fullWidth
            error={Boolean(errors.credentialUrl)}
            helperText={errors.credentialUrl?.message}
            {...register('credentialUrl')}
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Issued on"
            type="date"
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
            error={Boolean(errors.issuedOn)}
            helperText={errors.issuedOn?.message}
            {...register('issuedOn')}
          />
          <TextField
            label="Expires on"
            type="date"
            fullWidth
            slotProps={{ inputLabel: { shrink: true } }}
            error={Boolean(errors.expiresOn)}
            helperText={errors.expiresOn?.message}
            {...register('expiresOn')}
          />
        </Stack>
      </Stack>
    </FormDialog>
  );
}
